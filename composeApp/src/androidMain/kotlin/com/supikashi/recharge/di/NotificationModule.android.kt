package com.supikashi.recharge.di

import com.supikashi.recharge.notifications.NotificationScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationScheduler(get()) }
}
