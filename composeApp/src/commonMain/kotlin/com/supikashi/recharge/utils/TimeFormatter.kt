package com.supikashi.recharge.utils

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.day_friday
import recharge.composeapp.generated.resources.day_monday
import recharge.composeapp.generated.resources.day_saturday
import recharge.composeapp.generated.resources.day_sunday
import recharge.composeapp.generated.resources.day_thursday
import recharge.composeapp.generated.resources.day_tuesday
import recharge.composeapp.generated.resources.day_wednesday
import recharge.composeapp.generated.resources.month_april
import recharge.composeapp.generated.resources.month_august
import recharge.composeapp.generated.resources.month_december
import recharge.composeapp.generated.resources.month_february
import recharge.composeapp.generated.resources.month_genitive_april
import recharge.composeapp.generated.resources.month_genitive_august
import recharge.composeapp.generated.resources.month_genitive_december
import recharge.composeapp.generated.resources.month_genitive_february
import recharge.composeapp.generated.resources.month_genitive_january
import recharge.composeapp.generated.resources.month_genitive_july
import recharge.composeapp.generated.resources.month_genitive_june
import recharge.composeapp.generated.resources.month_genitive_march
import recharge.composeapp.generated.resources.month_genitive_may
import recharge.composeapp.generated.resources.month_genitive_november
import recharge.composeapp.generated.resources.month_genitive_october
import recharge.composeapp.generated.resources.month_genitive_september
import recharge.composeapp.generated.resources.month_january
import recharge.composeapp.generated.resources.month_july
import recharge.composeapp.generated.resources.month_june
import recharge.composeapp.generated.resources.month_march
import recharge.composeapp.generated.resources.month_may
import recharge.composeapp.generated.resources.month_november
import recharge.composeapp.generated.resources.month_october
import recharge.composeapp.generated.resources.month_september

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

@Composable
fun formatDate(date: LocalDate): String {
    val months = listOf(
        stringResource(Res.string.month_genitive_january),
        stringResource(Res.string.month_genitive_february),
        stringResource(Res.string.month_genitive_march),
        stringResource(Res.string.month_genitive_april),
        stringResource(Res.string.month_genitive_may),
        stringResource(Res.string.month_genitive_june),
        stringResource(Res.string.month_genitive_july),
        stringResource(Res.string.month_genitive_august),
        stringResource(Res.string.month_genitive_september),
        stringResource(Res.string.month_genitive_october),
        stringResource(Res.string.month_genitive_november),
        stringResource(Res.string.month_genitive_december)
    )
    return "${date.dayOfMonth} ${months[date.monthNumber - 1]}"
}

@Composable
fun formatMonth(date: LocalDate): String {
    val months = listOf(
        stringResource(Res.string.month_january),
        stringResource(Res.string.month_february),
        stringResource(Res.string.month_march),
        stringResource(Res.string.month_april),
        stringResource(Res.string.month_may),
        stringResource(Res.string.month_june),
        stringResource(Res.string.month_july),
        stringResource(Res.string.month_august),
        stringResource(Res.string.month_september),
        stringResource(Res.string.month_october),
        stringResource(Res.string.month_november),
        stringResource(Res.string.month_december)
    )
    return months[date.monthNumber - 1]
}

@Composable
fun formatDayOfWeek(date: LocalDate): String {
    return when (date.dayOfWeek.ordinal) {
        0 -> stringResource(Res.string.day_monday)
        1 -> stringResource(Res.string.day_tuesday)
        2 -> stringResource(Res.string.day_wednesday)
        3 -> stringResource(Res.string.day_thursday)
        4 -> stringResource(Res.string.day_friday)
        5 -> stringResource(Res.string.day_saturday)
        6 -> stringResource(Res.string.day_sunday)
        else -> ""
    }
}
