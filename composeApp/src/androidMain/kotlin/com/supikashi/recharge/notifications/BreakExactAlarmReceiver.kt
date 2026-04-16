package com.supikashi.recharge.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BreakExactAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 0)
        val title = intent.getStringExtra("title") ?: "Время перерыва!"
        val message = intent.getStringExtra("message") ?: "Пора отдохнуть"
        val isStartNotification = intent.getBooleanExtra("is_start_notification", true)
        
        BreakNotificationHelper.showNotification(context, notificationId, title, message, isStartNotification)
    }
}
