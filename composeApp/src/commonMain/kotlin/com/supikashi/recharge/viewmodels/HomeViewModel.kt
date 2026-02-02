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

    private val _currentBreak = MutableStateFlow<Break?>(null)
    val currentBreak: StateFlow<Break?> = _currentBreak.asStateFlow()

    override fun onCleared() {
        println("cleared")
        super.onCleared()
    }

    init {
        println(helloWorld)
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

    private fun startTimeUpdateLoop() {
        viewModelScope.launch {
            while (true) {
                delay(30_000) 
                val breaks = dao.getAllBreaksSorted()
                updateCurrentBreak(breaks)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun updateCurrentBreak(breaks: List<Break>) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date
        val currentTimeMinutes = now.hour * 60 + now.minute

        val activeBreak = breaks.firstOrNull { breakItem ->
            breakItem.date == today &&
            !breakItem.isCompleted &&
            breakItem.time <= currentTimeMinutes &&
            currentTimeMinutes < breakItem.time + 5 
        }
        
        _currentBreak.value = activeBreak
    }

    fun resetCounter() {
        viewModelScope.launch {
            userPreferencesRepository.resetAllPreferences()
        }
    }
}