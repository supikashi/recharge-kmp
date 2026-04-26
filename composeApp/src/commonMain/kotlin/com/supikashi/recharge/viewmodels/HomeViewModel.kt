package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val helloWorld: String,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val database: TaskDatabase
) : ViewModel() {
    private val dao = database.taskDao()

    val isFirstScheduleVisit: StateFlow<Boolean> = userPreferencesRepository.isFirstScheduleVisit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val shouldShowHomeSurvey: StateFlow<Boolean> = userPreferencesRepository.shouldShowHomeSurvey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _currentBreak = MutableStateFlow<Break?>(null)
    val currentBreak: StateFlow<Break?> = _currentBreak.asStateFlow()

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
        observeCurrentBreak()
        startTimeUpdateLoop()
    }

    private fun observeCurrentBreak() {
        viewModelScope.launch {
            dao.getAllBreaksFlow().collect { breaks ->
                updateCurrentBreak(breaks)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun startTimeUpdateLoop() {
        viewModelScope.launch {
            while (true) {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val secondsUntilNextMinute = 60 - now.second

                if (secondsUntilNextMinute == 60) {
                    delay(1000)
                } else {
                    delay(secondsUntilNextMinute * 1000L)
                }

                val breaks = dao.getAllBreaksSorted()
                updateCurrentBreak(breaks)
            }
        }
    }

    fun refreshCurrentBreak() {
        viewModelScope.launch {
            val breaks = dao.getAllBreaksSorted()
            updateCurrentBreak(breaks)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun updateCurrentBreak(breaks: List<Break>) {
        println("update")
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date
        val currentTimeMinutes = now.hour * 60 + now.minute

        val activeBreak = breaks.firstOrNull { breakItem ->
            breakItem.date == today &&
            !breakItem.isCompleted &&
            breakItem.time <= currentTimeMinutes &&
            currentTimeMinutes < breakItem.time + 10
        }
        
        _currentBreak.value = activeBreak
    }
}