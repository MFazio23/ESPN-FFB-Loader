package dev.mfazio.espnffb.handlers.charts

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.charts.ChartData
import dev.mfazio.espnffb.types.charts.ChartType
import dev.mfazio.espnffb.types.charts.LineChartAxis
import dev.mfazio.espnffb.types.charts.LineChartData
import dev.mfazio.espnffb.types.charts.SeriesDataEntry

const val yearlyMemberPointsPlusKey = "yearly-member-points-plus"

fun generateYearlyMemberPointsPlusChartData(
    scoreboards: List<dev.mfazio.espnffb.types.espn.ESPNScoreboard>,
    matchups: List<Matchup>,
    teamsMap: TeamYearMap,
    members: List<Member>
): List<ChartData> {
    val pointsScoredPlus =
        ESPNRecordBookCalculator.getPointsPlus(matchups, includePlayoffs = true, skipCurrentYear = true)
    val pointsAllowedPlus =
        ESPNRecordBookCalculator.getPointsAllowedPlus(matchups, includePlayoffs = true, skipCurrentYear = true)

    val yearlyPointsScoredPlus = pointsScoredPlus.groupBy { it.season }
    val yearlyPointsAllowedPlus = pointsAllowedPlus.groupBy { it.season }

    val yearlyPointsPlus = yearlyPointsScoredPlus.mapValues { (year, points) ->
        points to (yearlyPointsAllowedPlus[year] ?: emptyList())
    }

    val minPointsPlus = minOf(pointsScoredPlus.minOf { it.value }, pointsAllowedPlus.minOf { it.value })
    val maxPointsPlus = maxOf(pointsScoredPlus.maxOf { it.value }, pointsAllowedPlus.maxOf { it.value })

    return members.map { member ->
        val memberPointsPlus = yearlyPointsPlus.mapValues { (year, yearResults) ->
            val yearTeams = teamsMap[year] ?: return@mapValues null
            val team = yearTeams.firstOrNull { it.owners.contains(member.id) } ?: return@mapValues null

            val (yearPointsScoredPlus, yearPointsAllowedPlus) = yearResults

            yearPointsScoredPlus.firstOrNull { it.recordHolders.keys.contains(team.id) }?.value to
                yearPointsAllowedPlus.firstOrNull { it.recordHolders.keys.contains(team.id) }?.value
        }
        LineChartData(
            type = ChartType.Line,
            chartId = "$yearlyMemberPointsPlusKey-${member.id}",
            dataset = memberPointsPlus.mapNotNull { (year, yearPointsPlus) ->
                if (year == null || yearPointsPlus == null) return@mapNotNull null
                val (yearPointsScoredPlus, yearPointsAllowedPlus) = yearPointsPlus
                mapOf(
                    "year" to year,
                    "pointsScoredPlus" to yearPointsScoredPlus,
                    "pointsAllowedPlus" to yearPointsAllowedPlus
                )
            },
            seriesData = listOf(
                SeriesDataEntry(
                    dataKey = "pointsScoredPlus",
                    label = "Points Scored+",
                ),
                SeriesDataEntry(
                    dataKey = "pointsAllowedPlus",
                    label = "Points Allowed+",
                ),
            ),
            xAxis = LineChartAxis(
                dataKey = "year",
                min = ESPNConfig.historicalStartYear.toDouble(),
                max = ESPNConfig.currentYear.toDouble(),
                tickMinStep = 1.0,
            ),
            yAxis = LineChartAxis(
                min = minPointsPlus,
                max = maxPointsPlus,
            )
        )
    }
}
