package dev.mfazio.espnffb.types

data class Matchup(
    val id: Int,
    val year: Int,
    val week: Int,
    val home: Team,
    val away: Team,
    val homeScores: TeamScores,
    val awayScores: TeamScores,
)
