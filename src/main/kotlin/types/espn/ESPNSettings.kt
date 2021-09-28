package dev.mfazio.espnffb.types.espn

data class ESPNSettings(
    val isCustomizable: Boolean,
    val isPublic: Boolean,
    val scheduleSettings: ESPNScheduleSettings,
    val scoringSettings: ESPNScoringSettings
)