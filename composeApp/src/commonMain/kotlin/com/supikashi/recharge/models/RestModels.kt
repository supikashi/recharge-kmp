package com.supikashi.recharge.models

import org.jetbrains.compose.resources.DrawableResource

enum class RestType {
    ACTIVE, CALM, CREATIVE
}

data class CardContent(
    val title: String,
    val description: String,
    val additional: String? = null,
    val image: DrawableResource? = null
)

data class RestActivity(
    val name: String,
    val durationMin: Int,
    val durationMax: Int,
    val steps: List<CardContent>
)
