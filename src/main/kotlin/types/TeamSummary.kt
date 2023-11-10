package dev.mfazio.espnffb.types

data class TeamSummary(
    val teamId: Int,
    val teamName: String,
    val wins: Int,
    val losses: Int,
    val championships: Int = 0,
    val eras: List<TeamEra> = emptyList(),
)
