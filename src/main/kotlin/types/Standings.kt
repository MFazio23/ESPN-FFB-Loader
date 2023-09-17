package dev.mfazio.espnffb.types

data class Standings(
    val member: Member? = null,
    val teamId: Int? = null,
    val seasons: StandingsIntEntry? = null,
    val wins: StandingsIntEntry? = null,
    val losses: StandingsIntEntry? = null,
    val pointsScored: StandingsDoubleEntry? = null,
    val pointsAgainst: StandingsDoubleEntry? = null,
    val championships: StandingsIntEntry? = null,
    val playoffApps: StandingsIntEntry? = null,
    val championshipApps: StandingsIntEntry? = null,
)
