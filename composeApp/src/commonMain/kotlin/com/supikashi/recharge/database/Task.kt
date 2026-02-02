package com.supikashi.recharge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity
data class Task @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val startTime: Int = 0,
    val endTime: Int = 0,
    val isWork: Boolean = false,
    val isSplittable: Boolean = false,
)