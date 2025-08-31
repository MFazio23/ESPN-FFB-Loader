package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNTeamMatchupStatus(
    val adjustment: Double,
    val cumulativeScore: ESPNCumulativeScore? = null,
    val pointsByScoringPeriod: Map<String, Double>?,
    val rosterForCurrentScoringPeriod: ESPNRosterForCurrentScoringPeriod?,
    val rosterForMatchupPeriod: ESPNRosterForMatchupPeriod?,
    val teamId: Int,
    val tiebreak: Double,
    val totalPoints: Double,
    val totalPointsLive: Double? = null,
    val totalProjectedPointsLive: Double? = null,
)
