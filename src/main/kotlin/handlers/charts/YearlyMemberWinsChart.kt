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

const val yearlyMemberWinsKey = "yearly-member-wins"

fun generateYearlyMemberWinsChartData(
    scoreboards: List<ESPNScoreboard>,
    matchups: List<Matchup>,
    teamsMap: TeamYearMap,
    members: List<Member>
): List<ChartData> {
    val yearlyWins = ESPNStandingsCalculator.getPerSeasonResults(scoreboards, members, matchups, teamsMap)
    return members.mapNotNull { member ->
        val memberWins = yearlyWins[member] ?: return@mapNotNull null
        LineChartData(
            type = ChartType.Line,
            chartId = "$yearlyMemberWinsKey-${member.id}",
            dataset = memberWins.map { (year, yearResult) ->
                mapOf(
                    "year" to year,
                    "wins" to yearResult.regularSeasonWins + yearResult.playoffWins,
                    "regularSeasonWins" to yearResult.regularSeasonWins,
                    "playoffWins" to yearResult.playoffWins,
                    "isChampion" to yearResult.isChampion
                )
            },
            seriesData = listOf(
                SeriesDataEntry(
                    dataKey = "regularSeasonWins",
                    label = "Regular Season",
                    stack = "wins",
                ),
                SeriesDataEntry(
                    dataKey = "playoffWins",
                    label = "Playoffs",
                    stack = "wins",
                ),
            ),
            xAxis = LineChartAxis(
                dataKey = "year",
                min = ESPNConfig.historicalStartYear.toDouble(),
                max = ESPNConfig.currentYear.toDouble(),
                tickMinStep = 1.0,
            ),
            yAxis = LineChartAxis(
                dataKey = "wins",
                min = 0.0,
                max = memberWins.maxOf { (_, result) -> result.totalGames }.toDouble(),
            )
        )
    }
}
