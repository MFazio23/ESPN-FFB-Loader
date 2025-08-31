package dev.mfazio.espnffb.handlers

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.charts.*
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
        )
    }

    private fun generateYearlyMemberWinsChartData(
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
                ),
                yAxis = LineChartAxis(
                    dataKey = "wins",
                    min = 0.0,
                    max = memberWins.maxOf { (_, result) -> result.totalGames }.toDouble(),
                )
            )
        }
    }

    private fun generateYearlyMemberStandingChartData(
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

    const val yearlyMemberWinsKey = "yearly-member-wins"
    const val yearlyMemberStandingKey = "yearly-member-standing"
}
