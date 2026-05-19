package com.supikashi.recharge.background

import android.content.Context

class AndroidTimerBackgroundService(
    context: Context,
) : TimerBackgroundService {
    private val appContext = context.applicationContext

    override fun start(endTimeMillis: Long) {
        TimerForegroundService.start(appContext, endTimeMillis)
    }

    override fun stop() {
        TimerForegroundService.stop(appContext)
    }
}
