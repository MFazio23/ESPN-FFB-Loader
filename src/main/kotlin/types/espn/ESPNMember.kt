package dev.mfazio.espnffb.types.espn

data class ESPNMember(
    val displayName: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    @Transient
    val notificationSettings: List<ESPNNotificationSetting>? = null,
)