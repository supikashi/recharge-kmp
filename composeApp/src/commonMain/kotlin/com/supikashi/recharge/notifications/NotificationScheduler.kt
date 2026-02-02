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
    val timeInMinutes: Int 
)

@Composable
expect fun RequestNotificationPermission(
    onPermissionResult: (Boolean) -> Unit
)

expect class NotificationScheduler {
    fun hasPermission(): Boolean

    fun scheduleBreakNotification(notification: BreakNotification)

    fun cancelNotification(notificationId: Int)

    fun cancelNotificationsForTask(taskId: Int)

    fun cancelAllNotifications()
}

fun Break.toNotification(taskName: String): BreakNotification {
    return BreakNotification(
        id = id,
        taskId = taskId,
        title = "Время перерыва!",
        message = "Пора отдохнуть от задачи \"$taskName\"",
        date = date,
        timeInMinutes = time
    )
}
