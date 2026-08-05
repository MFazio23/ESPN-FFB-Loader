package dev.mfazio.espnffb.types

data class TeamMatchupResult(
    val year: Int,
    val week: Int,
    val teamId: Int,
    val matchupId: Int,
    val standardScore: Double,
    val projectedScore: Double? = null,
    val bestBallScore: Double? = null,
) {
    companion object {
        fun fromMatchup(matchup: Matchup, matchupId: Int): List<TeamMatchupResult> = listOf(
            TeamMatchupResult(
                year = matchup.year,
                week = matchup.week,
                teamId = matchup.homeTeamId,
                matchupId = matchupId,
                standardScore = matchup.homeScores.standardScore,
                bestBallScore = matchup.homeScores.bestBallScore,
                projectedScore = matchup.getProjectedScore(matchup.homeTeamId)
            ),
            TeamMatchupResult(
                year = matchup.year,
                week = matchup.week,
                teamId = matchup.awayTeamId,
                matchupId = matchupId,
                standardScore = matchup.awayScores.standardScore,
                bestBallScore = matchup.awayScores.bestBallScore,
                projectedScore = matchup.getProjectedScore(matchup.awayTeamId)
            )
        )
    }
}