package com.supikashi.recharge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity
data class Puzzle(
    @PrimaryKey val id: Int,
    val collectedPieces: Int = 0,
    val completedDate: LocalDate? = null
)
