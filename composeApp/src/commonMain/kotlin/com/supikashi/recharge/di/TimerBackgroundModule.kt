package com.supikashi.recharge.di

import com.supikashi.recharge.background.TimerBackgroundService
import org.koin.dsl.module

fun timerBackgroundModule(timerBackgroundService: TimerBackgroundService) = module {
    single<TimerBackgroundService> { timerBackgroundService }
}
