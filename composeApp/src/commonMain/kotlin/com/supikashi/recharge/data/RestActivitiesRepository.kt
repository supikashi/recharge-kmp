package com.supikashi.recharge.data

import com.supikashi.recharge.models.RestActivity
import com.supikashi.recharge.models.RestType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import recharge.composeapp.generated.resources.Res

class RestActivitiesRepository(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun getActivitiesForType(type: RestType, locale: String): List<RestActivity> {
        val fileName = if (locale.startsWith("ru", ignoreCase = true)) {
            "activities_ru.json"
        } else {
            "activities_en.json"
        }

        val jsonString = Res.readBytes("files/$fileName").decodeToString()
        val activitiesMap = json.decodeFromString<Map<String, List<RestActivity>>>(jsonString)

        return activitiesMap[type.name].orEmpty()
    }
}
