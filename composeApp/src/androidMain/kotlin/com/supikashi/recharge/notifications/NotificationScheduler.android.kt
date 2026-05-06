package com.supikashi.recharge.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

import android.app.AlarmManager
import android.os.SystemClock

actual class NotificationScheduler(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "break_notifications"
        const val CHANNEL_NAME = "Уведомления о перерывах"
        const val CHANNEL_DESCRIPTION = "Напоминания о запланированных перерывах"
        
        private const val KEY_NOTIFICATION_ID = "notification_id"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_TASK_ID = "task_id"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    actual fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    @OptIn(ExperimentalTime::class)
    actual fun scheduleBreakNotification(notification: BreakNotification) {
        println("android actual")
        val now = Clock.System.now()

        val timeZone = TimeZone.currentSystemDefault()
        
        val hours = notification.timeInMinutes / 60
        val minutes = notification.timeInMinutes % 60

        val scheduledTime = notification.date
            .atTime(hours, minutes)
            .toInstant(timeZone)
        
        val triggerTime = scheduledTime.toEpochMilliseconds()

        if (triggerTime <= now.toEpochMilliseconds()) return
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BreakExactAlarmReceiver::class.java).apply {
            putExtra("notification_id", notification.id)
            putExtra("title", notification.title)
            putExtra("message", notification.message)
            putExtra("task_id", notification.taskId)
            putExtra("is_start_notification", true)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                println("alarmManager.setAlarmClock 1")
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                println("alarmManager.setAndAllowWhileIdle")
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            println("alarmManager.setAlarmClock 2")
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        }
    }

    actual fun scheduleExactBreakNotification(notification: ExactBreakNotification) {
        println("android actual exact")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BreakExactAlarmReceiver::class.java).apply {
            putExtra("notification_id", notification.id)
            putExtra("title", notification.title)
            putExtra("message", notification.message)
            putExtra("task_id", notification.taskId)
            putExtra("is_start_notification", false)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerTime = notification.timeMillis
        
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, pendingIntent)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                println("alarmManager.setAlarmClock 1")
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                println("alarmManager.setAndAllowWhileIdle")
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            println("alarmManager.setAlarmClock 2")
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        }
    }

    private fun cancelAlarm(notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BreakExactAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
    
    actual fun cancelNotification(notificationId: Int) {
        cancelAlarm(notificationId)
    }
    
    actual fun cancelNotificationsForTask(taskId: Int) {
        
        
    }
    
    actual fun cancelAllNotifications() {
        
    }
}
