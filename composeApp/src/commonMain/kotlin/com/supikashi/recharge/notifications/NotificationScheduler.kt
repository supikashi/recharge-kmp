package com.supikashi.recharge.notifications

import androidx.compose.runtime.Composable
import com.supikashi.recharge.database.Break
import kotlinx.datetime.LocalDate

data class BreakNotification(
    val id: Int,
    val taskId: Int,
    val title: String,
    val message: String,
    val date: LocalDate,
    val timeInMinutes: Int,
    val isPrimary: Boolean = false
)

data class ExactBreakNotification(
    val id: Int,
    val taskId: Int,
    val title: String,
    val message: String,
    val timeMillis: Long,
)

@Composable
expect fun RequestNotificationPermission(
    onPermissionResult: (Boolean) -> Unit
)

expect class NotificationScheduler {
    fun hasPermission(): Boolean

    fun scheduleBreakNotification(notification: BreakNotification)
    
    fun scheduleExactBreakNotification(notification: ExactBreakNotification)

    fun cancelNotification(notificationId: Int)

    fun cancelNotificationsForTask(taskId: Int)

    fun cancelAllNotifications()
}

fun Break.toNotification(isPrimary: Boolean = false): BreakNotification {
    return BreakNotification(
        id = id,
        taskId = taskId,
        title = "Пришло время отдохнуть!",
        message = "Давай на минутку выйдем из потока — тело и мозг скажут спасибо",
        date = date,
        timeInMinutes = time,
        isPrimary = isPrimary
    )
}

fun Break.toEndNotification(duration: Int): BreakNotification {
    return BreakNotification(
        id = -id, // Use negative ID to distinguish from start notification
        taskId = taskId,
        title = "Перерыв окончен!",
        message = "Пора возвращаться к задачам",
        date = date,
        timeInMinutes = time + duration
    )
}
