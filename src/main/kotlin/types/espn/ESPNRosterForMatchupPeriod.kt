package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNRosterForMatchupPeriod(
    val appliedStatTotal: Double? = null,
    val entries: List<ESPNEntry>
)