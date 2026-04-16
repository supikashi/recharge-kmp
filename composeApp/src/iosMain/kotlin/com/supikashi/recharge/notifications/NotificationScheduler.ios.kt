package com.supikashi.recharge.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

actual class NotificationScheduler {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    
    actual fun hasPermission(): Boolean {
        return true
    }
    
    @OptIn(ExperimentalForeignApi::class, ExperimentalTime::class)
    actual fun scheduleBreakNotification(notification: BreakNotification) {
        println("ios actual")
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setSound(UNNotificationSound.defaultSound)
            val userInfoMap = mutableMapOf<Any?, Any>()
            userInfoMap["is_start_notification"] = true
            setUserInfo(userInfoMap)
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

    @OptIn(ExperimentalForeignApi::class, ExperimentalTime::class)
    actual fun scheduleExactBreakNotification(notification: ExactBreakNotification) {
        println("ios actual exact")
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.message)
            setSound(UNNotificationSound.defaultSound)
        }

        val currentMillis = Clock.System.now().toEpochMilliseconds()
        val delaySeconds = ((notification.timeMillis - currentMillis) / 1000.0)
        
        if (delaySeconds <= 0) return
        
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = delaySeconds,
            repeats = false
        )
        
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "break_${notification.id}",
            content = content,
            trigger = trigger
        )
        
        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to schedule exact notification: ${error.localizedDescription}")
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
