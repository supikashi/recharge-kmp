package com.supikashi.recharge.background

interface TimerBackgroundService {
    fun start(endTimeMillis: Long)

    fun stop()
}

class NoOpTimerBackgroundService : TimerBackgroundService {
    override fun start(endTimeMillis: Long) = Unit

    override fun stop() = Unit
}
