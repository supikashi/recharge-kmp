package com.supikashi.recharge.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object PomodoroSelection : Screen

    @Serializable
    data object Schedule : Screen

    @Serializable
    data object Rest : Screen

    @Serializable
    data class RestActivities(val type: String) : Screen

    @Serializable
    data object Statistics : Screen

    @Serializable
    data object Task : Screen

    @Serializable
    data object BreakNotification : Screen

    @Serializable
    data class BreakResult(val type: String, val durationMinutes: Int = 0) : Screen
}

enum class BreakResultType {
    CANCELLED,
    POSTPONED,
    STARTED
}
