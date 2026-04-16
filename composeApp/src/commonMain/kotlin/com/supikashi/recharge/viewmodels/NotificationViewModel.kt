package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.notifications.NotificationScheduler
import com.supikashi.recharge.notifications.toNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class NotificationViewModel(
    database: TaskDatabase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val dao = database.taskDao()
    
    companion object {
        const val MAX_NOTIFICATIONS = 50
    }

    val breaks: StateFlow<List<Break>> =
        dao.getAllBreaksFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _hasNotificationPermission = MutableStateFlow(false)
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private var previousBreaks: Set<Break> = emptySet()
    
    init {
        _hasNotificationPermission.value = notificationScheduler.hasPermission()

        viewModelScope.launch {
            breaks.collect { currentBreaks ->
                println("collect breaks")
                val currentBreaksSet = currentBreaks.toSet()
                if (previousBreaks.map { it.id } != currentBreaksSet.map { it.id }) {
                    println("reschedule breaks")
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    cancelNotifications(
                        breaks = currentBreaksSet.plus(previousBreaks)
                            .filter { it.date >= today }
                    )
                    scheduleNotifications(currentBreaks)
                    previousBreaks = currentBreaksSet
                }
            }
        }
    }
    
    fun setNotificationPermission(granted: Boolean) {
        _hasNotificationPermission.value = granted
    }

    private suspend fun cancelNotifications(breaks: List<Break>) {
        println("🗑️ [NotificationVM] Canceling notifications for ${breaks.size} deleted breaks: ${breaks.map {
            val timeHours = it.time / 60
            val timeMinutes = it.time % 60
            " ${timeHours.toString().padStart(2, '0')}:${timeMinutes.toString().padStart(2, '0')}"
        }}")
        breaks.forEach {
            notificationScheduler.cancelNotification(it.id)
        }
        dao.resetAllNotificationFlags()
        println("✅ [NotificationVM] Canceled ${breaks.size} notifications")
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun scheduleNotifications(currentBreaks: List<Break>) {
        println("📅 [NotificationVM] scheduleNotifications() called - full reschedule")
        
        if (!_hasNotificationPermission.value) {
            println("❌ [NotificationVM] No permission, skipping")
            return
        }

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentTimeMinutes = now.minute + now.hour * 60
        
        println("📅 [NotificationVM] Today: $today, currentTimeMinutes: $currentTimeMinutes")

        val breaksToNotify = dao.getNextBreaksWithLimit(today = today, currentTimeMinutes = currentTimeMinutes, limit = MAX_NOTIFICATIONS)
        if (breaksToNotify.isEmpty()) {
            println("ℹ️ [NotificationVM] No breaks to notify")
            return
        }
        
        println("📅 [NotificationVM] Scheduling ${breaksToNotify.size} closest breaks")
        
        breaksToNotify.forEachIndexed { index, breakItem ->
            val timeHours = breakItem.time / 60
            val timeMinutes = breakItem.time % 60
            println("🔔 [NotificationVM] Scheduling on ${breakItem.date} at ${timeHours.toString().padStart(2, '0')}:${timeMinutes.toString().padStart(2, '0')}")
            
            val isPrimary = index == 0
            notificationScheduler.scheduleBreakNotification(breakItem.toNotification(isPrimary))
            
            dao.updateNotificationScheduled(breakItem.id, true)
        }
        
        println("✅ [NotificationVM] Scheduled ${breaksToNotify.size} notifications")
    }
}
