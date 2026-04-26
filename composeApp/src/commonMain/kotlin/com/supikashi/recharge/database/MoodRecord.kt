package com.supikashi.recharge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity
data class MoodRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val timeMinutes: Int, 
    val value: Int 
)