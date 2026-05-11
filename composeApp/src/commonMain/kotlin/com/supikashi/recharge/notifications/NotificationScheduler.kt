package com.supikashi.recharge.notifications

import androidx.compose.runtime.Composable
import com.supikashi.recharge.database.Break
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.getString
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.notification_break_ended_desc
import recharge.composeapp.generated.resources.notification_break_ended_title
import recharge.composeapp.generated.resources.notification_time_to_rest_desc
import recharge.composeapp.generated.resources.notification_time_to_rest_title

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

suspend fun Break.toNotification(isPrimary: Boolean = false): BreakNotification {
    return BreakNotification(
        id = id,
        taskId = taskId,
        title = getString(Res.string.notification_time_to_rest_title),
        message = getString(Res.string.notification_time_to_rest_desc),
        date = date,
        timeInMinutes = time,
        isPrimary = isPrimary
    )
}

suspend fun Break.toEndNotification(duration: Int): BreakNotification {
    return BreakNotification(
        id = -id, 
        taskId = taskId,
        title = getString(Res.string.notification_break_ended_title),
        message = getString(Res.string.notification_break_ended_desc),
        date = date,
        timeInMinutes = time + duration
    )
}
