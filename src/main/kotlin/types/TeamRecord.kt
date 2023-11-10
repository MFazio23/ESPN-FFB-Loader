package dev.mfazio.espnffb.types

data class TeamRecord(
    val games: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val playoffWins: Int = 0,
    val playoffLosses: Int = 0,
    val pointsFor: Double = 0.0,
    val pointsAgainst: Double = 0.0,
    val playoffPointsFor: Double = 0.0,
    val playoffPointsAgainst: Double = 0.0,
    val averagePointsFor: Double = 0.0,
    val averagePointsAgainst: Double = 0.0,
    val averagePlayoffPointsFor: Double = 0.0,
    val averagePlayoffPointsAgainst: Double = 0.0,
)
