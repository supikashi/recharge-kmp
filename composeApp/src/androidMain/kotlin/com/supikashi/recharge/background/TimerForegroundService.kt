package com.supikashi.recharge.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.supikashi.recharge.R

class TimerForegroundService : Service() {
    private val stopHandler = Handler(Looper.getMainLooper())
    private val stopRunnable = Runnable { stopTimer() }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTimer()
            return START_NOT_STICKY
        }

        val endTimeMillis = intent?.getLongExtra(EXTRA_END_TIME_MILLIS, 0L) ?: 0L
        if (endTimeMillis <= System.currentTimeMillis()) {
            stopTimer()
            return START_NOT_STICKY
        }

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(endTimeMillis),
            foregroundServiceType()
        )
        scheduleStop(endTimeMillis)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopHandler.removeCallbacks(stopRunnable)
        super.onDestroy()
    }

    private fun buildNotification(endTimeMillis: Long): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.rest_timer_notification_title))
            .setContentText(getString(R.string.rest_timer_notification_text))
            .setContentIntent(contentPendingIntent())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setWhen(endTimeMillis)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .build()
    }

    private fun contentPendingIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("recharge://main")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        } ?: Intent(Intent.ACTION_VIEW, Uri.parse("recharge://main")).apply {
            setPackage(packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            this,
            CONTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleStop(endTimeMillis: Long) {
        val delayMillis = endTimeMillis - System.currentTimeMillis()
        stopHandler.removeCallbacks(stopRunnable)
        // The notification countdown is handled by Android's chronometer; this is only a one-shot cleanup.
        stopHandler.postDelayed(stopRunnable, delayMillis.coerceAtLeast(0L))
    }

    private fun stopTimer() {
        stopHandler.removeCallbacks(stopRunnable)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.rest_timer_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.rest_timer_channel_description)
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun foregroundServiceType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }
    }

    companion object {
        private const val ACTION_START = "com.supikashi.recharge.action.START_REST_TIMER"
        private const val ACTION_STOP = "com.supikashi.recharge.action.STOP_REST_TIMER"
        private const val EXTRA_END_TIME_MILLIS = "extra_end_time_millis"
        private const val CHANNEL_ID = "rest_timer"
        private const val NOTIFICATION_ID = 20_001
        private const val CONTENT_REQUEST_CODE = 20_002

        fun start(context: Context, endTimeMillis: Long) {
            val intent = Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_END_TIME_MILLIS, endTimeMillis)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
