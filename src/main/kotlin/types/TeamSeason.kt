package dev.mfazio.espnffb.types

data class TeamSeason(
    val year: Int,
    val team: Team,
    val owners: List<Member>,
    val wins: Int,
    val losses: Int,
    val isChampion: Boolean = false,
)
