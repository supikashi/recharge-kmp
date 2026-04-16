package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.notifications.ExactBreakNotification
import com.supikashi.recharge.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BreakNotificationViewModel(
    private val database: TaskDatabase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {
    private val dao = database.taskDao()

    private val _currentBreak = MutableStateFlow<Break?>(null)
    val currentBreak: StateFlow<Break?> = _currentBreak.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val breakDuration = userPreferencesRepository.selectedPomodoroType.map { it?.restMinutes ?: 0 }

    private var pomodoroType : PomodoroType? = null

    init {
        loadCurrentBreak()
        viewModelScope.launch {
            pomodoroType = userPreferencesRepository.selectedPomodoroType.first()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun loadCurrentBreak() {
        viewModelScope.launch {
            _isLoading.value = true
            val breaks = dao.getAllBreaksSorted()
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val today = now.date
            val currentTimeMinutes = now.hour * 60 + now.minute

            val activeBreak = breaks.firstOrNull { breakItem ->
                breakItem.date == today &&
                        !breakItem.isCompleted &&
                        breakItem.time <= currentTimeMinutes &&
                        currentTimeMinutes <= breakItem.time + 10
            }

            _currentBreak.value = activeBreak
            _isLoading.value = false
        }
    }

    @OptIn(ExperimentalTime::class)
    fun markBreakCompleted() {
        viewModelScope.launch {
            _currentBreak.value?.let { breakItem ->
                dao.markBreakCompleted(breakItem.id)
                val duration = pomodoroType?.restMinutes ?: 5
                
                val exactEndMillis = Clock.System.now().toEpochMilliseconds() + duration * 60 * 1000L
                
                val endNotification = ExactBreakNotification(
                    id = -breakItem.id,
                    taskId = breakItem.taskId,
                    title = "Перерыв окончен!",
                    message = "Пора возвращаться к задачам",
                    timeMillis = exactEndMillis
                )
                
                notificationScheduler.scheduleExactBreakNotification(endNotification)
            }
        }
    }

    fun postponeBreak() {
        viewModelScope.launch {
            _currentBreak.value?.let { current ->
                pomodoroType?.let { dao.postponeBreak(current, it.restMinutes) }
            }
        }
    }

    fun cancelBreak() {
        viewModelScope.launch {
            _currentBreak.value?.let { breakItem ->
                dao.markBreakCompleted(breakItem.id)
            }
        }
    }
}
