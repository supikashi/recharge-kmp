package com.supikashi.recharge.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BreakExactAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 0)
        val title = intent.getStringExtra("title") ?: "Break time!"
        val message = intent.getStringExtra("message") ?: "Let's go rest"
        val isStartNotification = intent.getBooleanExtra("is_start_notification", true)
        
        BreakNotificationHelper.showNotification(context, notificationId, title, message, isStartNotification)
    }
}
