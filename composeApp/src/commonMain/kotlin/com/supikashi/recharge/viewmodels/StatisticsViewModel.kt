package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.PuzzleRepository
import com.supikashi.recharge.database.Puzzle
import com.supikashi.recharge.database.PuzzleDayStatus
import com.supikashi.recharge.database.TaskDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class DailyBreakStats(
    val totalBreaks: Int = 0,
    val completedBreaks: Int = 0,
    val postponedBreaks: Int = 0,
    val cancelledBreaks: Int = 0,
    val averageDelaySeconds: Int? = null
) {
    val completionPercentage: Float
        get() = if (totalBreaks > 0) completedBreaks.toFloat() / totalBreaks else 0f
}

data class DayMoodStats(
    val date: LocalDate,
    val morningCount: Int = 0,
    val morningSum: Int = 0,
    val dayCount: Int = 0,
    val daySum: Int = 0,
    val eveningCount: Int = 0,
    val eveningSum: Int = 0,
) {
    val avg: Float get() = if (morningCount + dayCount + eveningCount > 0) {
        (morningSum.toFloat() + daySum.toFloat() + eveningSum.toFloat()) / (morningCount + dayCount + eveningCount)
    } else {
        0f
    }
    val morningAvg: Float get() = if (morningCount > 0) morningSum.toFloat() / morningCount else 0f
    val dayAvg: Float get() = if (dayCount > 0) daySum.toFloat() / dayCount else 0f
    val eveningAvg: Float get() = if (eveningCount > 0) eveningSum.toFloat() / eveningCount else 0f
    
    val isEmpty: Boolean get() = morningCount == 0 && dayCount == 0 && eveningCount == 0
}

data class MoodTrendStats(
    val days: List<DayMoodStats> = emptyList()
) {
    val isEmpty: Boolean get() = days.all { it.isEmpty }
}

class StatisticsViewModel(
    database: TaskDatabase,
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {
    private val dao = database.taskDao()
    
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    @OptIn(ExperimentalTime::class)
    fun saveMood(value: Int) {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val timeMinutes = now.hour * 60 + now.minute
            dao.insertMoodRecord(com.supikashi.recharge.database.MoodRecord(
                date = now.date,
                timeMinutes = timeMinutes,
                value = value
            ))
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyStats: StateFlow<DailyBreakStats> = _selectedDate
        .flatMapLatest { date ->
            if (date != null) {
                dao.getBreaksByDate(date.toString())
                    .map { breaks ->
                        val restBreaks = breaks.filter { it.isForBreak }
                        val completedRestBreaks = restBreaks.filter { it.isCompleted && !it.isCancelled }
                        val delays = completedRestBreaks.mapNotNull { it.delaySeconds }
                        val averageDelay = if (delays.isNotEmpty()) delays.sum() / delays.size else null
                        DailyBreakStats(
                            totalBreaks = restBreaks.size,
                            completedBreaks = completedRestBreaks.size,
                            postponedBreaks = restBreaks.sumOf { it.postponeCount },
                            cancelledBreaks = restBreaks.count { it.isCancelled },
                            averageDelaySeconds = averageDelay
                        )
                    }
            } else {
                flowOf(DailyBreakStats())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyBreakStats())

    val puzzles: StateFlow<List<Puzzle>> = puzzleRepository.getPuzzles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val puzzleDayStatuses: StateFlow<List<PuzzleDayStatus>> = puzzleRepository.getPuzzleDayStatuses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDebugPuzzleDayStatus(date: LocalDate, status: String) {
        viewModelScope.launch {
            puzzleRepository.setDebugDayStatus(date, status)
        }
    }

    fun resetDebugPuzzleState() {
        viewModelScope.launch {
            puzzleRepository.resetDebugPuzzleState()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyMoodStats: StateFlow<MoodTrendStats> = _selectedDate
        .flatMapLatest { date ->
            if (date != null) {
                val startDate = date.plus(-6, DateTimeUnit.DAY)
                dao.getMoodRecordsByDateRange(startDate, date).map { records ->
                        val grouped = records.groupBy { it.date }
                        
                        val days = (0..6).map { offset ->
                            val currentDate = startDate.plus(offset, DateTimeUnit.DAY)
                            val dayRecords = grouped[currentDate] ?: emptyList()
                            
                            var mCount = 0
                            var mSum = 0
                            var dCount = 0
                            var dSum = 0
                            var eCount = 0
                            var eSum = 0
                            
                            dayRecords.forEach { record ->
                                when {
                                    record.timeMinutes < 720 -> { mCount++; mSum += record.value }
                                    record.timeMinutes < 1080 -> { dCount++; dSum += record.value }
                                    else -> { eCount++; eSum += record.value }
                                }
                            }
                            
                            DayMoodStats(
                                date = currentDate,
                                morningCount = mCount,
                                morningSum = mSum,
                                dayCount = dCount,
                                daySum = dSum,
                                eveningCount = eCount,
                                eveningSum = eSum
                            )
                        }
                        
                        MoodTrendStats(days)
                    }

            } else {
                flowOf(MoodTrendStats())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MoodTrendStats())
}
