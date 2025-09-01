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
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

const val yearlyMemberStandingKey = "yearly-member-standing"

fun generateYearlyMemberStandingChartData(
    scoreboards: List<ESPNScoreboard>,
    matchups: List<Matchup>,
    teamsMap: TeamYearMap,
    members: List<Member>
): List<ChartData> {
    val seasonResults = ESPNStandingsCalculator.getPerSeasonResults(scoreboards, members, matchups, teamsMap)
    return members.mapNotNull { member ->
        val memberResults = seasonResults[member] ?: return@mapNotNull null
        LineChartData(
            type = ChartType.Line,
            chartId = "$yearlyMemberStandingKey-${member.id}",
            dataset = memberResults.map { (year, yearResult) ->
                mapOf(
                    "year" to year,
                    "finalStanding" to yearResult.finalSeasonStanding,
                    "isChampion" to yearResult.isChampion
                )
            },
            seriesData = listOf(
                SeriesDataEntry(
                    dataKey = "finalStanding",
                    label = "Final Standing",
                ),
            ),
            xAxis = LineChartAxis(
                dataKey = "year",
                min = ESPNConfig.historicalStartYear.toDouble(),
                max = ESPNConfig.currentYear.toDouble(),
                tickMinStep = 1.0,
            ),
            yAxis = LineChartAxis(
                dataKey = "finalStanding",
                min = 1.0,
                max = 12.0,
                reverse = true
            )
        )
    }
}