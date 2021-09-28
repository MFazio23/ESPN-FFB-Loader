package dev.mfazio.espnffb.types.espn

data class ESPNRecord(
    val away: ESPNTeamStanding,
    val division: ESPNDivisionStatus,
    val home: ESPNTeamStanding,
    val overall: ESPNOverall
)