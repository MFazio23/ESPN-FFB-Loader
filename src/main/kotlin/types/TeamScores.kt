package dev.mfazio.espnffb.types

data class TeamScores(
    val standardScore: Double,
    val bestBallScore: Double? = null,
) {
    fun getBestBallGap() = (bestBallScore ?: standardScore) - standardScore
}

val standardScoreFunc: (TeamScores) -> Double = { scores -> scores.standardScore }
val bestBallScoreFunc: (TeamScores) -> Double = { scores -> scores.bestBallScore ?: scores.standardScore }
val bestBallGapFunc: (TeamScores) -> Double = { scores -> scores.getBestBallGap() }

typealias MappedTeamScores = List<Pair<Int, TeamScores>>