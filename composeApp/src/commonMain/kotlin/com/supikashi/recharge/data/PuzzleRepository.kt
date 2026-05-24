package com.supikashi.recharge.data

import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.Puzzle
import com.supikashi.recharge.database.PuzzleDayStatus
import com.supikashi.recharge.database.PuzzleDayStatusValue
import com.supikashi.recharge.database.TaskDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object PuzzleRules {
    const val PIECES_PER_PUZZLE = 7
}

class PuzzleRepository(
    private val database: TaskDatabase
) {
    private val dao = database.taskDao()
    private val updateDayStatusesMutex = Mutex()

    fun getPuzzles(): Flow<List<Puzzle>> = dao.getPuzzlesFlow()

    fun getPuzzleDayStatuses(): Flow<List<PuzzleDayStatus>> = dao.getPuzzleDayStatusesFlow()

    @OptIn(ExperimentalTime::class)
    suspend fun updateDayStatuses() {
        updateDayStatusesMutex.withLock {
            updateDayStatusesLocked()
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun setDebugDayStatus(date: LocalDate, status: String) {
        require(status in PuzzleDayStatusValue.all) { "Unsupported puzzle day status: $status" }

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (date >= today) return

        updateDayStatusesMutex.withLock {
            val dayStatus = PuzzleDayStatus(date = date, status = status)
            val dayStatuses = dao.getPuzzleDayStatusesSorted()
                .filterNot { it.date == date } + dayStatus
            val puzzles = buildPuzzlesFromStatuses(dayStatuses)

            dao.replacePuzzleDebugState(dayStatus, puzzles)
        }
    }

    suspend fun resetDebugPuzzleState() {
        updateDayStatusesMutex.withLock {
            dao.resetPuzzleDebugState()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun updateDayStatusesLocked() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date
        val latestStatus = dao.getLatestPuzzleDayStatus()

        //ensurePuzzlesInitializedFromStatuses()

        if (latestStatus != null) {
            var date = latestStatus.date.plus(1, DateTimeUnit.DAY)
            while (date < today) {
                if (dao.getPuzzleDayStatusByDate(date.toString()) == null) {
                    savePastDayStatus(date)
                }
                date = date.plus(1, DateTimeUnit.DAY)
            }
        }

        val todayStatus = dao.getPuzzleDayStatusByDate(today.toString())
        if (
            todayStatus?.status == PuzzleDayStatusValue.SUCCESS ||
            todayStatus?.status == PuzzleDayStatusValue.FAIL
        ) {
            return
        }

        saveTodayStatus(today, now)
    }

    private suspend fun savePastDayStatus(date: LocalDate) {
        val breaks = getRestBreaks(date)
        val status = when {
            breaks.isEmpty() -> PuzzleDayStatusValue.SKIPED
            breaks.all { it.isCompleted } -> PuzzleDayStatusValue.SUCCESS
            else -> PuzzleDayStatusValue.FAIL
        }

        saveDayStatus(date, status)
    }

    private suspend fun saveTodayStatus(today: LocalDate, now: LocalDateTime) {
        val breaks = getRestBreaks(today)

        when {
            breaks.isEmpty() -> {
                saveDayStatus(today, PuzzleDayStatusValue.SKIPED)
            }

            breaks.all { it.isCompleted } -> {
                saveDayStatus(today, PuzzleDayStatusValue.SUCCESS)
            }

            breaks.any { it.isMissed(now) } -> {
                saveDayStatus(today, PuzzleDayStatusValue.FAIL)
            }

            else -> {
                dao.deletePuzzleDayStatus(today.toString())
            }
        }
    }

    private suspend fun saveDayStatus(date: LocalDate, status: String) {
        val existingStatus = dao.getPuzzleDayStatusByDate(date.toString())
        if (existingStatus?.status == status) return

        dao.upsertPuzzleDayStatus(PuzzleDayStatus(date = date, status = status))
        updatePuzzleProgress(status, date)
    }

    private suspend fun ensurePuzzlesInitializedFromStatuses() {
        if (dao.getLatestPuzzle() != null) return

        dao.getPuzzleDayStatusesSorted().forEach { dayStatus ->
            updatePuzzleProgress(dayStatus.status, dayStatus.date)
        }
    }

    private suspend fun updatePuzzleProgress(status: String, date: LocalDate) {
        when (status) {
            PuzzleDayStatusValue.SUCCESS -> addPuzzlePiece(date)
            PuzzleDayStatusValue.FAIL -> resetCurrentPuzzle()
        }
    }

    private suspend fun addPuzzlePiece(date: LocalDate) {
        val currentPuzzle = currentPuzzleForProgress() ?: return
        val collectedPieces = (currentPuzzle.collectedPieces + 1)
            .coerceAtMost(PuzzleRules.PIECES_PER_PUZZLE)

        dao.upsertPuzzle(
            currentPuzzle.copy(
                collectedPieces = collectedPieces,
                completedDate = if (collectedPieces == PuzzleRules.PIECES_PER_PUZZLE) date else null
            )
        )
    }

    private suspend fun resetCurrentPuzzle() {
        val currentPuzzle = currentPuzzleForProgress() ?: return

        dao.upsertPuzzle(
            currentPuzzle.copy(
                collectedPieces = 0,
                completedDate = null
            )
        )
    }

    private suspend fun currentPuzzleForProgress(): Puzzle? {
        val latestPuzzle = dao.getLatestPuzzle()
        val availablePuzzleCount = PuzzleAssetCatalog.availablePuzzleCount

        if (availablePuzzleCount == 0) return null

        return when {
            latestPuzzle == null -> Puzzle(id = 1)
            latestPuzzle.completedDate != null && latestPuzzle.id < availablePuzzleCount -> {
                Puzzle(id = latestPuzzle.id + 1)
            }
            latestPuzzle.completedDate != null -> null
            latestPuzzle.id > availablePuzzleCount -> null
            else -> latestPuzzle
        }
    }

    private fun buildPuzzlesFromStatuses(dayStatuses: List<PuzzleDayStatus>): List<Puzzle> {
        val availablePuzzleCount = PuzzleAssetCatalog.availablePuzzleCount
        if (availablePuzzleCount == 0) return emptyList()

        val puzzles = mutableMapOf<Int, Puzzle>()
        var currentPuzzle: Puzzle? = Puzzle(id = 1)

        dayStatuses.sortedBy { it.date }.forEach { dayStatus ->
            val puzzle = currentPuzzle ?: return@forEach

            when (dayStatus.status) {
                PuzzleDayStatusValue.SUCCESS -> {
                    val collectedPieces = (puzzle.collectedPieces + 1)
                        .coerceAtMost(PuzzleRules.PIECES_PER_PUZZLE)
                    val updatedPuzzle = puzzle.copy(
                        collectedPieces = collectedPieces,
                        completedDate = if (collectedPieces == PuzzleRules.PIECES_PER_PUZZLE) {
                            dayStatus.date
                        } else {
                            null
                        }
                    )

                    puzzles[updatedPuzzle.id] = updatedPuzzle
                    currentPuzzle = when {
                        updatedPuzzle.completedDate != null &&
                            updatedPuzzle.id < availablePuzzleCount -> {
                            Puzzle(id = updatedPuzzle.id + 1)
                        }

                        updatedPuzzle.completedDate != null -> null
                        else -> updatedPuzzle
                    }
                }

                PuzzleDayStatusValue.FAIL -> {
                    val updatedPuzzle = puzzle.copy(
                        collectedPieces = 0,
                        completedDate = null
                    )
                    puzzles[updatedPuzzle.id] = updatedPuzzle
                    currentPuzzle = updatedPuzzle
                }
            }
        }

        return puzzles.values.sortedBy { it.id }
    }

    private suspend fun getRestBreaks(date: LocalDate): List<Break> {
        return dao.getBreaksByDateSync(date.toString()).filter { it.isForBreak }
    }

    private fun Break.isMissed(now: LocalDateTime): Boolean {
        if (isCompleted) return false

        val currentSeconds = (now.hour * 60 + now.minute) * 60 + now.second
        val missAfterSeconds = (time + BREAK_GRACE_MINUTES) * 60
        println("break missed ${currentSeconds > missAfterSeconds}")
        return currentSeconds > missAfterSeconds
    }

    private companion object {
        const val BREAK_GRACE_MINUTES = 10
    }
}
