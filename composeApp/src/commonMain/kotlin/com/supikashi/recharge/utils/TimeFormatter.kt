package com.supikashi.recharge.utils

import kotlinx.datetime.LocalDate

fun formatMinutesToTime(minutes: Int): String {
    val hours = (minutes / 60).coerceIn(0, 23)
    val mins = (minutes % 60).coerceIn(0, 59)
    val h = if (hours < 10) "0$hours" else hours.toString()
    val m = if (mins < 10) "0$mins" else mins.toString()
    return "$h:$m"
}

fun parseTimeToMinutes(time: String): Int? {
    if (time.length != 5 || time[2] != ':') return null
    
    val parts = time.split(":")
    if (parts.size != 2) return null
    
    val hours = parts[0].toIntOrNull() ?: return null
    val minutes = parts[1].toIntOrNull() ?: return null

    if (hours !in 0..23 || minutes !in 0..59) return null
    
    return hours * 60 + minutes
}

fun formatDate(date: LocalDate): String {
    val months = listOf(
        "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
        "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"
    )
    return "${date.dayOfMonth} ${months[date.monthNumber - 1]}"
}

fun formatDayOfWeek(date: LocalDate): String {
    return when (date.dayOfWeek.ordinal) {
        0 -> "Понедельник"
        1 -> "Вторник"
        2 -> "Среда"
        3 -> "Четверг"
        4 -> "Пятница"
        5 -> "Суббота"
        6 -> "Воскресенье"
        else -> ""
    }
}
