package dev.mfazio.espnffb.types

import kotlin.math.absoluteValue

data class Matchup(
    val id: Int,
    val year: Int,
    val week: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val homeScores: TeamScores,
    val awayScores: TeamScores,
    val homePlayers: List<Player>? = null,
    val awayPlayers: List<Player>? = null,
    val playoffTierType: PlayoffTierType? = null,
    val isHomeOriginalWinner: Boolean = true,
) {
    fun homeTeam(teamsMap: Map<Int, List<Team>>) = teamsMap[year]?.firstOrNull { it.id == homeTeamId }
    fun awayTeam(teamsMap: Map<Int, List<Team>>) = teamsMap[year]?.firstOrNull { it.id == awayTeamId }
    fun includesTeam(teamId: Int) = teamId == homeTeamId || teamId == awayTeamId
    fun didTeamWin(teamId: Int) = didTeamWin(listOf(teamId))
    fun didTeamWin(teamIds: List<Int>) =
        (teamIds.contains(homeTeamId) && (homeScores.standardScore > awayScores.standardScore || homeScores.standardScore == awayScores.standardScore && isHomeOriginalWinner)) ||
            (teamIds.contains(awayTeamId) && (homeScores.standardScore < awayScores.standardScore || homeScores.standardScore == awayScores.standardScore && !isHomeOriginalWinner))

    fun getTeamScores(teamIds: List<Int>) = when {
        teamIds.contains(homeTeamId) -> homeScores
        teamIds.contains(awayTeamId) -> awayScores
        else -> null
    }

    fun getOtherTeamScores(teamIds: List<Int>) = when {
        !teamIds.contains(homeTeamId) && !teamIds.contains(awayTeamId) -> null
        teamIds.contains(awayTeamId) -> homeScores
        else -> awayScores
    }

    fun isClose(threshold: Int = 10): Boolean {
        val homeScore = homeScores.standardScore
        val awayScore = awayScores.standardScore

        return (homeScore - awayScore).absoluteValue <= threshold
    }

    fun isLowScoring(threshold: Int = 115): Boolean {
        val homeScore = homeScores.standardScore
        val awayScore = awayScores.standardScore

        return homeScore <= threshold && awayScore <= threshold
    }
}
