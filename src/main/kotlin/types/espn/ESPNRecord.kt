package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNRecord(
    val away: ESPNTeamStanding,
    val division: ESPNDivisionStatus,
    val home: ESPNTeamStanding,
    val overall: ESPNOverall
)