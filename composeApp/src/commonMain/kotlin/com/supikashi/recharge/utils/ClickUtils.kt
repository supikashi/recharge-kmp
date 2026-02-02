package com.supikashi.recharge.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun rememberDebounceClickHandler(
    debounceMs: Long = 500L,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return {
        println(lastClickTime)
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (abs(currentTime - lastClickTime) >= debounceMs) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
