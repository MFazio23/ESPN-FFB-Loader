package dev.mfazio.espnffb.handlers.charts

import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.charts.ChartData
import dev.mfazio.espnffb.types.espn.ESPNScoreboard

object ChartsHandler {
    fun generateChartData(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
        members: List<Member>
    ): Map<String, List<ChartData>> {
        return mapOf(
            yearlyMemberWinsKey to generateYearlyMemberWinsChartData(scoreboards, matchups, teamsMap, members),
            yearlyMemberStandingKey to generateYearlyMemberStandingChartData(scoreboards, matchups, teamsMap, members),
            yearlyMemberPointsKey to generateYearlyMemberPointsChartData(scoreboards, matchups, teamsMap, members),
            yearlyMemberPointsPlusKey to generateYearlyMemberPointsPlusChartData(
                scoreboards,
                matchups,
                teamsMap,
                members
            ),
            yearlyPositionalPointsKey to generateYearlyPositionalPointsChart(scoreboards, matchups, teamsMap, members)
        )
    }
}