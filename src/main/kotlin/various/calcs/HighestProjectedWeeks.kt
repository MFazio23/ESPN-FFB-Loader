package dev.mfazio.espnffb.various.calcs

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.TeamYearMap
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.various.VariousFactCard
import dev.mfazio.espnffb.various.VariousFactEntry
import dev.mfazio.espnffb.various.VariousFactGenerator
import dev.mfazio.utils.extensions.orZero
import dev.mfazio.utils.extensions.toTwoDigits

object HighestProjectedWeeks : VariousFactGenerator {
    override fun generate(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard> {
        val highestProjectedWeeks = calculateHighestProjectedWeeks(scoreboards, matchups, members, allTeams, teamsMap)

        return listOf(
            VariousFactCard(
                title = "Highest Projections",
                subtitle = "Weeks with the highest projected score",
                entries = highestProjectedWeeks.take(12).mapIndexed { index, entry ->
                    VariousFactEntry(
                        number = index + 1,
                        title = "${entry.team?.fullName} - ${entry.projectedScore?.toTwoDigits()} points",
                        subtitle = "Week ${entry.week}, ${entry.year} (Actual: ${entry.actualScore?.toTwoDigits()} points)",
                        isCurrent = entry.year == ESPNConfig.currentYear,
                    )
                }
            )
        )
    }

    private fun calculateHighestProjectedWeeks(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        members: List<Member>,
        allTeams: List<Team>,
        teamsMap: TeamYearMap
    ): List<ProjectedWeekEntry> {
        val modernMatchups = matchups.filter { it.year >= ESPNConfig.modernStartYear }

        val projections = modernMatchups.flatMap { matchup ->
            val projectedHomeScore = matchup.homePlayers?.sumOf { player -> if (player.lineupSlot.isStarter()) player.projectedPoints ?: 0.0 else 0.0 }
            val projectedAwayScore = matchup.awayPlayers?.sumOf { player -> if (player.lineupSlot.isStarter()) player.projectedPoints ?: 0.0 else 0.0 }

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

        return projections.sortedByDescending { entry -> entry.projectedScore.orZero() }
    }
}