package dev.mfazio.espnffb.types.espn

data class ESPNSchedule(
    val away: ESPNTeamMatchupStatus,
    val home: ESPNTeamMatchupStatus,
    val id: Int,
    val matchupPeriodId: Int,
    val playoffTierType: String,
    val winner: String
)