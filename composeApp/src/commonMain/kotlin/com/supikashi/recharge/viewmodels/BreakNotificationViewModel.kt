package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.background.TimerBackgroundService
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.notifications.ExactBreakNotification
import com.supikashi.recharge.notifications.NotificationScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.notification_break_ended_desc
import recharge.composeapp.generated.resources.notification_break_ended_title
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BreakNotificationViewModel(
    private val database: TaskDatabase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationScheduler: NotificationScheduler,
    private val timerBackgroundService: TimerBackgroundService,
) : ViewModel() {
    private val dao = database.taskDao()

    @OptIn(ExperimentalTime::class)
    private val _currentTimeMillis = MutableStateFlow(Clock.System.now().toEpochMilliseconds())

    val _currentBreak: MutableStateFlow<Break?> = MutableStateFlow(null)
    @OptIn(ExperimentalTime::class)
    val currentBreak: StateFlow<Break?> = _currentBreak//combine(
//        dao.getAllBreaksFlow(),
//        _currentTimeMillis
//    ) { breaks, _ ->
//        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
//        val today = now.date
//        val currentTimeMinutes = now.hour * 60 + now.minute
//
//        breaks.firstOrNull { breakItem ->
//            breakItem.date == today &&
//                    !breakItem.isCompleted &&
//                    breakItem.time <= currentTimeMinutes &&
//                    currentTimeMinutes < breakItem.time + 10
//        }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val breakDuration = userPreferencesRepository.selectedPomodoroType.map { it?.restMinutes ?: 0 }

    private var pomodoroType : PomodoroType? = null

    init {
        viewModelScope.launch {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val breaks = dao.getNextBreaksFlow(now.date.toString()).first()
            val today = now.date
            val currentTimeMinutes = now.hour * 60 + now.minute

            _currentBreak.value = breaks.firstOrNull { breakItem ->
                breakItem.date == today &&
                        !breakItem.isCompleted &&
                        !breakItem.isCancelled &&
                        breakItem.time <= currentTimeMinutes &&
                        currentTimeMinutes < breakItem.time + 10
            }
            pomodoroType = userPreferencesRepository.selectedPomodoroType.first()
            _isLoading.value = false
        }
    }

    @OptIn(ExperimentalTime::class)
    fun markBreakCompleted() {
        viewModelScope.launch {
            currentBreak.value?.let { breakItem ->
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val currentSeconds = (now.hour * 60 + now.minute) * 60 + now.second
                val expectedSeconds = breakItem.time * 60
                val delay = kotlin.math.max(0, currentSeconds - expectedSeconds)

                dao.markBreakCompleted(breakItem.id, delay)
                val duration = pomodoroType?.restMinutes ?: 5
                
                val exactEndMillis = ((Clock.System.now().toEpochMilliseconds() + duration * 60 * 1000L + 999) / 1000) * 1000
                
                val endNotification = ExactBreakNotification(
                    id = -breakItem.id,
                    taskId = breakItem.taskId,
                    title = getString(Res.string.notification_break_ended_title),
                    message = getString(Res.string.notification_break_ended_desc),
                    timeMillis = exactEndMillis
                )
                
                notificationScheduler.scheduleExactBreakNotification(endNotification)
                userPreferencesRepository.setActiveRestEndTimestamp(exactEndMillis)
                timerBackgroundService.start(exactEndMillis)
            }
        }
    }

    fun postponeBreak() {
        viewModelScope.launch {
            currentBreak.value?.let { current ->
                pomodoroType?.let { dao.postponeBreak(current, it.restMinutes) }
            }
        }
    }

    fun cancelBreak() {
        viewModelScope.launch {
            currentBreak.value?.let { breakItem ->
                dao.markBreakCancelled(breakItem.id)
                userPreferencesRepository.setActiveRestEndTimestamp(0L)
            }
        }
    }
}
