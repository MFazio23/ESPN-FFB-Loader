package dev.mfazio.espnffb.types

data class Matchup(
    val id: Int,
    val year: Int,
    val week: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeScores: TeamScores,
    val awayScores: TeamScores,
)
