package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class NotificationViewModel(
    database: TaskDatabase,
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

    private var previousBreakIds: Set<Int> = emptySet()
    
    init {
        _hasNotificationPermission.value = notificationScheduler.hasPermission()

        viewModelScope.launch {
            breaks.collect { currentBreaks ->
                val currentIds = currentBreaks.map { it.id }.toSet()
                val hasNewBreaks = currentIds.any { it !in previousBreakIds }

                val deletedIds = previousBreakIds - currentIds
                if (deletedIds.isNotEmpty()) {
                    cancelNotifications(deletedIds)
                }

                previousBreakIds = currentIds
                if (hasNewBreaks) {
                    scheduleNotifications(currentBreaks)
                }
            }
        }
    }
    
    fun setNotificationPermission(granted: Boolean) {
        _hasNotificationPermission.value = granted
    }

    private fun cancelNotifications(breakIds: Set<Int>) {
        println("🗑️ [NotificationVM] Canceling notifications for ${breakIds.size} deleted breaks: $breakIds")
        breakIds.forEach { id ->
            notificationScheduler.cancelNotification(id)
        }
        println("✅ [NotificationVM] Canceled ${breakIds.size} notifications")
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

        val scheduledBreaks = currentBreaks.filter { it.isNotificationScheduled }
        if (scheduledBreaks.isNotEmpty()) {
            println("🗑️ [NotificationVM] Canceling ${scheduledBreaks.size} existing notifications")
            scheduledBreaks.forEach { breakItem ->
                notificationScheduler.cancelNotification(breakItem.id)
            }
        }

        dao.resetAllNotificationFlags()

        val breaksToNotify = dao.getBreaksWithoutNotification(today = today, currentTimeMinutes = currentTimeMinutes, limit = MAX_NOTIFICATIONS)
        if (breaksToNotify.isEmpty()) {
            println("ℹ️ [NotificationVM] No breaks to notify")
            return
        }
        
        println("📅 [NotificationVM] Scheduling ${breaksToNotify.size} closest breaks")

        val taskIds = breaksToNotify.map { it.taskId }.distinct()
        val tasks = taskIds.mapNotNull { taskId -> 
            dao.getTask(taskId)?.let { taskId to it.name }
        }.toMap()
        
        breaksToNotify.forEach { breakItem ->
            val taskName = tasks[breakItem.taskId] ?: "Задача"
            val timeHours = breakItem.time / 60
            val timeMinutes = breakItem.time % 60
            println("🔔 [NotificationVM] Scheduling: '$taskName' on ${breakItem.date} at ${timeHours.toString().padStart(2, '0')}:${timeMinutes.toString().padStart(2, '0')}")
            notificationScheduler.scheduleBreakNotification(breakItem.toNotification(taskName))
            dao.updateNotificationScheduled(breakItem.id, true)
        }
        
        println("✅ [NotificationVM] Scheduled ${breaksToNotify.size} notifications")
    }
}
