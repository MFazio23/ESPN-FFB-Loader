package dev.mfazio.espnffb.handlers.charts

import com.squareup.moshi.Json
import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Position
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.charts.*
import dev.mfazio.espnffb.types.espn.ESPNScoreboard

val positions = listOf(
    Position.QB,
    Position.RB,
    Position.WR,
    Position.TE,
    Position.K,
    Position.DEF
)

data class PositionalPointsChartData(
    val year: Int,
    val totalPoints: Double,
    @param:Json(name = "QB") val qbPoints: Double,
    @param:Json(name = "RB") val rbPoints: Double,
    @param:Json(name = "WR") val wrPoints: Double,
    @param:Json(name = "TE") val tePoints: Double,
    @param:Json(name = "K") val kPoints: Double,
    @param:Json(name = "DEF") val defPoints: Double,
)

const val yearlyPositionalPointsKey = "yearly-positional-points"

fun generateYearlyPositionalPointsChart(
    scoreboards: List<ESPNScoreboard>,
    matchups: List<Matchup>,
    teamsMap: TeamYearMap,
    members: List<Member>
): List<ChartData> {
    val yearlyMatchups = matchups.groupBy { it.year }.filter { it.key >= ESPNConfig.modernStartYear }

    val lineChartData = members.mapNotNull { member ->
        val memberMatchups = yearlyMatchups.mapValues { (year, yearMatchups) ->
            val yearTeams = teamsMap[year] ?: return@mapValues emptyList<Matchup>()
            val team =
                yearTeams.firstOrNull { it.owners.contains(member.id) } ?: return@mapValues emptyList<Matchup>()
            yearMatchups.filter { it.includesTeam(team.id) }
        }

        if (memberMatchups.values.all { it.isEmpty() }) {
            return@mapNotNull null
        }

        val memberPositionalPoints: Map<Int, Map<Position, Double>> = memberMatchups.mapValues { (year, matchups) ->
            val yearTeams = teamsMap[year] ?: return@mapValues emptyMap<Position, Double>()
            val team =
                yearTeams.firstOrNull { it.owners.contains(member.id) } ?: return@mapValues emptyMap<Position, Double>()

            positions.associateWith { position ->
                matchups.sumOf { matchup ->
                    val players = matchup.getPlayersByTeamId(team.id) ?: return@sumOf 0.0
                    players
                        .filter { player -> player.lineupSlot.isStarter() && player.position == position }
                        .sumOf { it.points }
                }
            }
        }

        if (memberPositionalPoints.values.all { it.isEmpty() }) {
            return@mapNotNull null
        }

        LineChartData(
            type = ChartType.Line,
            chartId = "$yearlyPositionalPointsKey-${member.id}",
            dataset = memberPositionalPoints.mapNotNull { (year, yearPointsPlus) ->
                if (yearPointsPlus.isEmpty()) return@mapNotNull null
                PositionalPointsChartData(
                    year = year,
                    totalPoints = yearPointsPlus.values.sum(),
                    qbPoints = yearPointsPlus[Position.QB] ?: 0.0,
                    rbPoints = yearPointsPlus[Position.RB] ?: 0.0,
                    wrPoints = yearPointsPlus[Position.WR] ?: 0.0,
                    tePoints = yearPointsPlus[Position.TE] ?: 0.0,
                    kPoints = yearPointsPlus[Position.K] ?: 0.0,
                    defPoints = yearPointsPlus[Position.DEF] ?: 0.0,
                )
            },
            seriesData = positions.map {
                SeriesDataEntry(
                    dataKey = it.name,
                    label = it.name,
                    stack = "totalPoints",
                    area = true,
                    showMark = false,
                )
            },
            xAxis = LineChartAxis(
                dataKey = "year",
                min = ESPNConfig.modernStartYear.toDouble(),
                // Don't include current year as it may be incomplete.
                max = ESPNConfig.currentYear.toDouble() - 1.0,
                tickMinStep = 1.0,
            ),
            yAxis = LineChartAxis(
                min = 0.0,
                //We'll get the proper league value after the fact
                max = 0.0,
            )
        )
    }

    val maxPoints = lineChartData.maxOf { data ->
        data.dataset.map { it as PositionalPointsChartData }.maxOf { it.totalPoints }
    }

    return lineChartData.map { data ->
        data.copy(
            yAxis = data.yAxis.copy(
                max = maxPoints,
            )
        )
    }
}