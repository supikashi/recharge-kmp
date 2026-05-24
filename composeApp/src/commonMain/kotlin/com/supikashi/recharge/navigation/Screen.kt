package com.supikashi.recharge.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data class Onboarding(val isFromSettings: Boolean = false) : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data class PomodoroSelection(val isFromSettings: Boolean = false) : Screen

    @Serializable
    data object Schedule : Screen

    @Serializable
    data object Rest : Screen

    @Serializable
    data class RestActivities(val type: String) : Screen

    @Serializable
    data class RestActivitiesList(val type: String) : Screen

    @Serializable
    data object Statistics : Screen

    @Serializable
    data object PuzzleCollection : Screen

    @Serializable
    data object Task : Screen

    @Serializable
    data object BreakNotification : Screen

    @Serializable
    data class BreakResult(val type: String, val durationMinutes: Int = 0) : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data class Calendar(val selectedDateEpochDays: Long) : Screen
}

enum class BreakResultType {
    CANCELLED,
    POSTPONED,
    STARTED
}
