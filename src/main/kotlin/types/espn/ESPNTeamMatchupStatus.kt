package dev.mfazio.espnffb.types.espn

data class ESPNTeamMatchupStatus(
    val adjustment: Double,
    val cumulativeScore: ESPNCumulativeScore,
    val pointsByScoringPeriod: Map<String, Double>?,
    val rosterForCurrentScoringPeriod: ESPNRosterForCurrentScoringPeriod?,
    val rosterForMatchupPeriod: ESPNRosterForMatchupPeriod?,
    val teamId: Int,
    val tiebreak: Double,
    val totalPoints: Double,
    val totalPointsLive: Double,
    val totalProjectedPointsLive: Double
)
