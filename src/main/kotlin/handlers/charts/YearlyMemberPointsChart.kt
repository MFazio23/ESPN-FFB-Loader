package dev.mfazio.espnffb.handlers.charts

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.charts.ChartData
import dev.mfazio.espnffb.types.charts.ChartType
import dev.mfazio.espnffb.types.charts.LineChartAxis
import dev.mfazio.espnffb.types.charts.LineChartData
import dev.mfazio.espnffb.types.charts.SeriesDataEntry
import dev.mfazio.espnffb.types.espn.ESPNScoreboard

const val yearlyMemberPointsKey = "yearly-member-points"

fun generateYearlyMemberPointsChartData(
    scoreboards: List<ESPNScoreboard>,
    matchups: List<Matchup>,
    teamsMap: TeamYearMap,
    members: List<Member>
): List<ChartData> {
    val seasonResults = ESPNStandingsCalculator.getPerSeasonResults(scoreboards, members, matchups, teamsMap)
    val allResults = seasonResults.flatMap { it.value.values }
    val minPoints = allResults.minOf { minOf(it.pointsScored, it.pointsAgainst) }
    val maxPoints = allResults.maxOf { maxOf(it.pointsScored, it.pointsAgainst) }
    return members.mapNotNull { member ->
        val memberResults = seasonResults[member] ?: return@mapNotNull null
        LineChartData(
            type = ChartType.Line,
            chartId = "$yearlyMemberPointsKey-${member.id}",
            dataset = memberResults.map { (year, yearResult) ->
                mapOf(
                    "year" to year,
                    "pointsScored" to yearResult.pointsScored,
                    "pointsAgainst" to yearResult.pointsAgainst,
                )
            },
            seriesData = listOf(
                SeriesDataEntry(
                    dataKey = "pointsScored",
                    label = "Points Scored",
                ),
                SeriesDataEntry(
                    dataKey = "pointsAgainst",
                    label = "Points Against",
                )
            ),
            xAxis = LineChartAxis(
                dataKey = "year",
                min = ESPNConfig.historicalStartYear.toDouble(),
                max = ESPNConfig.currentYear.toDouble(),
                tickMinStep = 1.0,
            ),
            yAxis = LineChartAxis(
                min = minPoints,
                max = maxPoints,
            )
        )
    }
}
