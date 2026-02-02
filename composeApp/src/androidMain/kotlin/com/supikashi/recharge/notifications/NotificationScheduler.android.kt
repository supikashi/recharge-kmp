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
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    
    private val workManager = WorkManager.getInstance(context)
    
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
        
        val delayMillis = scheduledTime.toEpochMilliseconds() - now.toEpochMilliseconds()

        if (delayMillis <= 0) return
        
        val inputData = Data.Builder()
            .putInt(KEY_NOTIFICATION_ID, notification.id)
            .putString(KEY_TITLE, notification.title)
            .putString(KEY_MESSAGE, notification.message)
            .putInt(KEY_TASK_ID, notification.taskId)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<BreakNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("break_${notification.id}")
            .addTag("task_${notification.taskId}")
            .build()
        
        workManager.enqueueUniqueWork(
            "break_notification_${notification.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    actual fun cancelNotification(notificationId: Int) {
        workManager.cancelAllWorkByTag("break_$notificationId")
    }
    
    actual fun cancelNotificationsForTask(taskId: Int) {
        workManager.cancelAllWorkByTag("task_$taskId")
    }
    
    actual fun cancelAllNotifications() {
        workManager.cancelAllWork()
    }
}

class BreakNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    
    override fun doWork(): Result {
        val notificationId = inputData.getInt("notification_id", 0)
        val title = inputData.getString("title") ?: "Время перерыва!"
        val message = inputData.getString("message") ?: "Пора отдохнуть"
        
        showNotification(notificationId, title, message)
        
        return Result.success()
    }
    
    private fun showNotification(id: Int, title: String, message: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("recharge://break_notification"),
            context,
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.component?.let {
                Class.forName(it.className)
            } ?: return
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(id, notification)
            }
        } else {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
    }
}
