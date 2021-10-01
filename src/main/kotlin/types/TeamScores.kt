package dev.mfazio.espnffb.types

data class TeamScores(
    val standardScore: Double,
    val bestBallScore: Double = standardScore
)
