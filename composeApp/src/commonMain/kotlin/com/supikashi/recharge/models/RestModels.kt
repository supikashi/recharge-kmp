package com.supikashi.recharge.models

import kotlinx.serialization.Serializable

@Serializable
enum class RestType {
    ACTIVE, CALM, CREATIVE
}

@Serializable
data class CardContent(
    val title: String,
    val description: String,
    val additional: String? = null,
    val imageId: String? = null
)

@Serializable
data class RestActivity(
    val name: String,
    val durationMin: Int,
    val durationMax: Int,
    val steps: List<CardContent>
)
