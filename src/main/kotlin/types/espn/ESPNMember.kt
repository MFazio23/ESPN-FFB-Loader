package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNMember(
    val displayName: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    @Transient
    val notificationSettings: List<ESPNNotificationSetting>? = null,
)