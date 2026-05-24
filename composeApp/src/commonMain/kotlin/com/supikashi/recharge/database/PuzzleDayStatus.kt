package com.supikashi.recharge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

object PuzzleDayStatusValue {
    const val SUCCESS = "success"
    const val FAIL = "fail"
    const val SKIPED = "skiped"

    val all = setOf(SUCCESS, FAIL, SKIPED)
}

@Entity
data class PuzzleDayStatus(
    @PrimaryKey val date: LocalDate,
    val status: String
)
