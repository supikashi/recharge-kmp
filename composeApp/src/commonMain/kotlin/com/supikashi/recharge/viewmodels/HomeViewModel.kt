package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.PuzzleRepository
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.TaskDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Instant

sealed class TimerState {
    data class Working(val nextBreakExpectedStartMillis: Long) : TimerState()
    data class Resting(val restEndMillis: Long) : TimerState()
    data object Idle : TimerState()
}

class HomeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val database: TaskDatabase,
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {
    private val dao = database.taskDao()

    val isFirstScheduleVisit: StateFlow<Boolean> = userPreferencesRepository.isFirstScheduleVisit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val shouldShowHomeSurvey: StateFlow<Boolean> = userPreferencesRepository.shouldShowHomeSurvey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalTime::class)
    private val _currentTimeMillis = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    private val breaksFlow = dao.getNextBreaksFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()).toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val tasksFlow = dao.getNextTasksFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()).toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    @OptIn(ExperimentalTime::class)
    val currentBreak: StateFlow<Break?> = combine(
        breaksFlow,
        _currentTimeMillis
    ) { breaks, currentTimeMillis ->

        val now = Instant.fromEpochMilliseconds(currentTimeMillis).toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date
        val currentTimeMinutes = now.hour * 60 + now.minute

        breaks.firstOrNull { breakItem ->
            breakItem.date == today &&
            !breakItem.isCompleted &&
            !breakItem.isCancelled &&
            breakItem.time <= currentTimeMinutes &&
            currentTimeMinutes < breakItem.time + 10
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalTime::class)
    val homeTimerState: StateFlow<TimerState> = combine(
        userPreferencesRepository.activeRestEndTimestamp,
        _currentTimeMillis,
        tasksFlow
    ) { restEndMillis, currentMillis, tasks ->
        println("tasks ${tasks.size}")
        if (restEndMillis > currentMillis) {
            TimerState.Resting(restEndMillis)
        } else {
            val now = Instant.fromEpochMilliseconds(currentMillis).toLocalDateTime(TimeZone.currentSystemDefault())
            val today = now.date
            val currentTimeMinutes = now.hour * 60 + now.minute
            
//            val nextBreak = breaks.firstOrNull { breakItem ->
//                breakItem.date == today &&
//                !breakItem.isCompleted &&
//                currentTimeMinutes < breakItem.time + 1
//            }
            val currentTask = tasks.firstOrNull { task ->
                task.task.date == today &&
                task.task.startTime <= currentTimeMinutes &&
                task.task.endTime > currentTimeMinutes &&
                task.task.isWork && task.task.isSplittable
            }

            val nextBreak = currentTask?.breaks?.firstOrNull { breakItem ->
                breakItem.date == today
                && !breakItem.isCompleted
                && !breakItem.isCancelled
                && currentTimeMinutes < breakItem.time + 10
            }

            if (nextBreak != null) {
                val breakStartMillis =
                    LocalDateTime(now.year, now.monthNumber, now.dayOfMonth, 0, 0)
                        .toInstant(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds() + (nextBreak.time * 60000L)
                TimerState.Working(breakStartMillis)
            } else if (currentTask != null) {
                val taskEndMillis =
                    LocalDateTime(now.year, now.monthNumber, now.dayOfMonth, 0, 0)
                        .toInstant(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds() + (currentTask.task.endTime * 60000L)
                TimerState.Working(taskEndMillis)
            } else {
                TimerState.Idle
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerState.Idle)

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

    init {
        viewModelScope.launch {
            userPreferencesRepository.ensureFirstLaunchTimestamp()
        }
        startTimeUpdateLoop()
        viewModelScope.launch {
            var lastPuzzleStatusUpdateMinute = -1L
            _currentTimeMillis.collect { timeMillis ->
                val currentMinute = timeMillis / 60000L
                if (currentMinute != lastPuzzleStatusUpdateMinute) {
                    lastPuzzleStatusUpdateMinute = currentMinute
                    puzzleRepository.updateDayStatuses()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun startTimeUpdateLoop() {
        viewModelScope.launch {
            while (true) {
                delay(1000L - _currentTimeMillis.value % 1000 + 2)
                _currentTimeMillis.value = Clock.System.now().toEpochMilliseconds()
                println(_currentTimeMillis.value)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun refreshCurrentBreak() {
        _currentTimeMillis.value = Clock.System.now().toEpochMilliseconds()
    }
}
