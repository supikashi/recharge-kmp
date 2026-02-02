package com.supikashi.recharge.models

enum class RestType {
    ACTIVE, CALM, CREATIVE
}

data class RestActivity(
    val name: String = "name",
    val description: String = "description",
    val durationMin: Int = 0,
    val durationMax: Int = 0,
    val instruction: String = "Instruction placeholder"
)
