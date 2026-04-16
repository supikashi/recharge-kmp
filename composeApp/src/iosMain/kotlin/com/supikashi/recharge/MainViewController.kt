package com.supikashi.recharge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.supikashi.recharge.database.getRoomDatabase
import com.supikashi.recharge.di.KoinInitializer

private val _shouldOpenBreakNotification = mutableStateOf(false)

var shouldOpenBreakNotificationScreen: Boolean
    get() = _shouldOpenBreakNotification.value
    set(value) {
        _shouldOpenBreakNotification.value = value
    }

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) {
    val dao = remember {
        getRoomDatabase(getDatabaseBuilder()).taskDao()
    }

    val shouldOpen by _shouldOpenBreakNotification
    
    App(
        taskDao = dao,
        shouldOpenBreakNotification = shouldOpen,
        onBreakNotificationNavigated = {
            shouldOpenBreakNotificationScreen = false
        }
    )
}