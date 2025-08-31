package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNSettings(
    val isCustomizable: Boolean,
    val isPublic: Boolean,
    val scheduleSettings: ESPNScheduleSettings,
    val scoringSettings: ESPNScoringSettings
)