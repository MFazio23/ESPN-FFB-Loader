package dev.mfazio.espnffb.types

data class TeamScores(
    val standardScore: Double,
    val bestBallScore: Double? = null,
) {
    fun getBestBallGap() = (bestBallScore ?: standardScore) - standardScore
}
