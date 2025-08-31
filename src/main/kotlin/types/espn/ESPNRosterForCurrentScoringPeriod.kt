package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNRosterForCurrentScoringPeriod(
    val appliedStatTotal: Double,
    val entries: List<ESPNEntry>
)