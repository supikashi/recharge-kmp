package com.supikashi.recharge

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.supikashi.recharge.database.getRoomDatabase


class MainActivity : ComponentActivity() {
    private var shouldOpenBreakNotification by mutableStateOf(false)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
        )
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        val dao = getRoomDatabase(getDatabaseBuilder(this)).taskDao()
        setContent {
            App(
                taskDao = dao,
                shouldOpenBreakNotification = shouldOpenBreakNotification
            )
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "recharge" && uri.host == "break_notification") {
                shouldOpenBreakNotification = true
            }
        }
    }
}
