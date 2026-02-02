package com.supikashi.recharge.models

enum class PomodoroType(
    val workMinutes: Int,
    val restMinutes: Int,
    val displayName: String
) {
    DEBUG(
        workMinutes = 2,
        restMinutes = 1,
        displayName = "Классический (25/5)"
    ),
    CLASSIC(
        workMinutes = 25,
        restMinutes = 5,
        displayName = "Классический (25/5)"
    ),
    EXTENDED(
        workMinutes = 52,
        restMinutes = 17,
        displayName = "Расширенный (52/17)"
    ),
    DEEP_WORK(
        workMinutes = 90,
        restMinutes = 30,
        displayName = "Глубокая работа (90/30)"
    );

    companion object {
        fun fromOrdinal(ordinal: Int): PomodoroType? {
            println("fromOrdinal ${ordinal} ${entries}")
            return entries.getOrNull(ordinal)
        }
    }
}
