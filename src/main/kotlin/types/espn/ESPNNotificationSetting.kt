package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNNotificationSetting(
    val enabled: Boolean,
    val id: String,
    val type: String
)