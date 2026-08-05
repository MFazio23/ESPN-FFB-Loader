package dev.mfazio.espnffb.various.calcs

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.types.getProjectedScore
import dev.mfazio.espnffb.various.VariousFactCard
import dev.mfazio.espnffb.various.VariousFactEntry
import dev.mfazio.espnffb.various.VariousFactGenerator
import dev.mfazio.utils.extensions.orZero
import dev.mfazio.utils.extensions.toTwoDigits

object LargestProjectionGaps : VariousFactGenerator {
    override fun generate(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard> {
        val largestProjectionGaps = calculateLargestProjectionGaps(scoreboards, matchups, members, allTeams, teamsMap)

        return listOf(
            VariousFactCard(
                title = "Largest Positive Projection Gaps",
                subtitle = "Weeks where teams most outperformed their projections",
                entries = largestProjectionGaps.take(12).mapIndexed { index, entry ->
                    mapToVariousFactEntry(index, entry)
                }
            ),
            VariousFactCard(
                title = "Largest Negative Projection Gaps",
                subtitle = "Weeks where teams most underperformed their projections",
                entries = largestProjectionGaps.takeLast(12).sortedBy { it.projectionGap }.mapIndexed { index, entry ->
                    mapToVariousFactEntry(index, entry)
                }
            ),
        )
    }

    private fun mapToVariousFactEntry(index: Int, entry: ProjectedWeekEntry) = VariousFactEntry(
        number = index + 1,
        title = "${entry.team?.fullName} - ${entry.projectionGap.toTwoDigits()} points",
        subtitle = "Week ${entry.week}, ${entry.year}: ${entry.actualScore?.toTwoDigits()} (A) vs. ${entry.projectedScore?.toTwoDigits()} (P)",
        isCurrent = entry.year == ESPNConfig.currentYear,
    )

    private fun calculateLargestProjectionGaps(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        members: List<Member>,
        allTeams: List<Team>,
        teamsMap: TeamYearMap
    ): List<ProjectedWeekEntry> {
        val modernMatchups = matchups.filter { it.year >= ESPNConfig.modernStartYear }

        val projections = modernMatchups.flatMap { matchup ->
            val projectedHomeScore = matchup.homePlayers?.getProjectedScore()
            val projectedAwayScore = matchup.awayPlayers?.getProjectedScore()

            listOf(
                ProjectedWeekEntry(
                    team = matchup.homeTeam(teamsMap),
                    week = matchup.week,
                    year = matchup.year,
                    projectedScore = projectedHomeScore,
                    actualScore = matchup.homeScores.standardScore,
                ),
                ProjectedWeekEntry(
                    team = matchup.awayTeam(teamsMap),
                    week = matchup.week,
                    year = matchup.year,
                    projectedScore = projectedAwayScore,
                    actualScore = matchup.awayScores.standardScore,
                )
            )
        }

        return projections.filter { it.projectedScore.orZero() >= 40.0 }.sortedByDescending { entry -> entry.projectionGap }
    }
}

data class ProjectedWeekEntry(
    val team: Team?,
    val year: Int,
    val week: Int,
    val projectedScore: Double?,
    val actualScore: Double?
) {
    val projectionGap: Double
        get() = (actualScore.orZero() - projectedScore.orZero())
}