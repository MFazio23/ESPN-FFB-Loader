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

object MostPerfectWeeks : VariousFactGenerator {
    override fun generate(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard> {
        val modernMatchups = matchups.filter { it.year >= ESPNConfig.modernStartYear }
        val modernTeamsMap = teamsMap.filterKeys { it >= ESPNConfig.modernStartYear }

        val memberPerfectWeeks = members.associateWith { member ->
            modernMatchups
                .filter { it.includesMember(member, modernTeamsMap) }
                .filter { matchup ->
                    val team = modernTeamsMap[matchup.year]?.firstOrNull { it.owners.contains(member.id) }
                        ?: return@filter false
                    val teamScores = matchup.getTeamScores(team.id) ?: return@filter false

                    teamScores.isIdealWeek()
                }
        }.filterValues { it.isNotEmpty() }.toList().sortedByDescending { (_, perfectWeeks) -> perfectWeeks.size }

        return listOf(
            VariousFactCard(
            title = mostPerfectWeeksTitle,
            subtitle = mostPerfectWeeksSubtitle,
            entries = memberPerfectWeeks.mapIndexed { index, (member, perfectWeeks) ->
                val perfectWeeksThisYear = perfectWeeks.filter { it.year == ESPNConfig.currentYear }
                VariousFactEntry(
                    number = index + 1,
                    title = member.fullName,
                    subtitle = "${perfectWeeks.size} perfect week${if (perfectWeeks.size != 1) "s" else ""} (${perfectWeeksThisYear.size} in ${ESPNConfig.currentYear})"
                )
            }
        ))
    }

    const val mostPerfectWeeksTitle = "Most Perfect Roster Weeks"
    const val mostPerfectWeeksSubtitle = "Weeks where a manager played their best possible lineup"
}