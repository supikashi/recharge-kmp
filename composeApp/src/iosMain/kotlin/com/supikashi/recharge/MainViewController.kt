package com.supikashi.recharge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.supikashi.recharge.database.getRoomDatabase
import com.supikashi.recharge.di.KoinInitializer

var shouldOpenBreakNotificationScreen: Boolean = false

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) {
    val dao = remember {
        getRoomDatabase(getDatabaseBuilder()).taskDao()
    }

    var shouldOpen by remember { mutableStateOf(shouldOpenBreakNotificationScreen) }
    
    App(
        taskDao = dao,
        shouldOpenBreakNotification = shouldOpen
    )

    shouldOpenBreakNotificationScreen = false
}