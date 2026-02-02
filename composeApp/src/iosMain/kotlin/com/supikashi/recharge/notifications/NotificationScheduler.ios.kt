package com.supikashi.recharge.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class NotificationScheduler {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    
    actual fun hasPermission(): Boolean {
        return true
    }
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun scheduleBreakNotification(notification: BreakNotification) {
        println("ios actual")
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setSound(UNNotificationSound.defaultSound)
        }

        val hours = notification.timeInMinutes / 60
        val minutes = notification.timeInMinutes % 60

        val dateComponents = NSDateComponents().apply {
            setYear(notification.date.year.toLong())
            setMonth(notification.date.monthNumber.toLong())
            setDay(notification.date.dayOfMonth.toLong())
            setHour(hours.toLong())
            setMinute(minutes.toLong())
            setSecond(0)
        }
        
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = false
        )
        
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "break_${notification.id}",
            content = content,
            trigger = trigger
        )
        
        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to schedule notification: ${error.localizedDescription}")
            }
        }
    }
    
    actual fun cancelNotification(notificationId: Int) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            listOf("break_$notificationId")
        )
    }
    
    actual fun cancelNotificationsForTask(taskId: Int) {
        notificationCenter.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val idsToRemove = requests
                ?.mapNotNull { it as? UNNotificationRequest }
                ?.filter { it.identifier.startsWith("break_") }
                ?.map { it.identifier }
                ?: emptyList()

            notificationCenter.removePendingNotificationRequestsWithIdentifiers(
                idsToRemove.filter { it.contains("task_$taskId") }
            )
        }
    }
    
    actual fun cancelAllNotifications() {
        notificationCenter.removeAllPendingNotificationRequests()
    }
}
