package com.supikashi.recharge.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class Break @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int = 0,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val time: Int = 0,
    val isForBreak: Boolean = true,
    val isCompleted: Boolean = false,
    val isNotificationScheduled: Boolean = false,
)
