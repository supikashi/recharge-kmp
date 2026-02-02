package com.supikashi.recharge

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds

class Greeting {
    private val platform = getPlatform()

    fun greet(): Flow<String> = flow {
        for (i in 1..100) {
            delay(1.seconds)
            emit("$i ${platform.name}")
        }
    }
}