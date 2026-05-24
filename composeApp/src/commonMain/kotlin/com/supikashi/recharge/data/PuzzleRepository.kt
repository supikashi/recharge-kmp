package com.supikashi.recharge.data

import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.PuzzleDayStatus
import com.supikashi.recharge.database.PuzzleDayStatusValue
import com.supikashi.recharge.database.TaskDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PuzzleRepository(
    private val database: TaskDatabase
) {
    private val dao = database.taskDao()
    private val updateDayStatusesMutex = Mutex()

    @OptIn(ExperimentalTime::class)
    suspend fun updateDayStatuses() {
        updateDayStatusesMutex.withLock {
            updateDayStatusesLocked()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun updateDayStatusesLocked() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date
        val latestStatus = dao.getLatestPuzzleDayStatus()

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

        dao.upsertPuzzleDayStatus(PuzzleDayStatus(date = date, status = status))
    }

    private suspend fun saveTodayStatus(today: LocalDate, now: LocalDateTime) {
        val breaks = getRestBreaks(today)

        when {
            breaks.isEmpty() -> {
                dao.upsertPuzzleDayStatus(
                    PuzzleDayStatus(date = today, status = PuzzleDayStatusValue.SKIPED)
                )
            }

            breaks.all { it.isCompleted } -> {
                dao.upsertPuzzleDayStatus(
                    PuzzleDayStatus(date = today, status = PuzzleDayStatusValue.SUCCESS)
                )
            }

            breaks.any { it.isMissed(now) } -> {
                dao.upsertPuzzleDayStatus(
                    PuzzleDayStatus(date = today, status = PuzzleDayStatusValue.FAIL)
                )
            }

            else -> {
                dao.deletePuzzleDayStatus(today.toString())
            }
        }
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
