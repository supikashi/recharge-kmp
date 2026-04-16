package com.supikashi.recharge.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.analytics.logEvent

object AnalyticsLogger {
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        Firebase.analytics.logEvent(eventName) {
            params.forEach { (key, value) ->
                param(key, value.toString())
            }
        }
    }
}
