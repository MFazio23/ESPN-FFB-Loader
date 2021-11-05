package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.*

object ESPNStandingsCalculator {

    //TODO: Convert to teamsMap
    fun getStandingsFromMatchups(
        matchups: List<Matchup>,
        members: List<Member>,
        teams: List<Team>,
        teamsMap: Map<Int, List<Team>> = emptyMap()
    ): List<Standings> =
        members.filter { !Standings.excludedMemberIds.contains(it.id) }.map { member ->
            Standings(
                member = member,
                seasons = getSeasonsForMember(member, teams),
                wins = getWinsForMember(matchups, member, teamsMap),
                losses = getLossesForMember(matchups, member, teams),
                pointsScored = getPointsScored(matchups, member, teams),
                pointsAgainst = getPointsAgainst(matchups, member, teams),
                championships = getChampionshipCount(matchups, member, teams),
                playoffApps = getPlayoffAppearanceCount(matchups, member, teamsMap),
            )
        }

    private fun getSeasonsForMember(member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) }
            .distinctBy { teams -> teams.year }
            .count()
            .let { count ->
                StandingsIntEntry(count, topSixRankings = count)
            }


    private fun getWinsForMember(
        matchups: List<Matchup>,
        member: Member,
        teamsMap: Map<Int, List<Team>>
    ): StandingsIntEntry {

        val memberTeams = teamsMap.flatMap { (_, teams) -> teams }.filter { it.owners.contains(member.id) }

        val standardScoringWins = memberTeams.let { teams ->
            matchups
                .filter { matchup ->
                    teams
                        .filter { it.year == matchup.year }
                        .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                }
                .count { matchup ->
                    matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                }
        }

        val topSixScoringWins = memberTeams.let { teams ->
            matchups
                .groupBy { it.year }
                .mapValues { (year, matchups) ->
                    val yearTeams = teamsMap[year]?.filter { it.owners.contains(member.id) }

                    if (yearTeams?.isNotEmpty() == true) {

                        matchups
                            .groupBy { it.week }
                            .mapValues { (_, matchups) ->
                                matchups
                                    .flatMap {
                                        listOf(it.awayTeamId to it.awayScores.standardScore) + listOf(it.homeTeamId to it.homeScores.standardScore)
                                    }
                                    .sortedByDescending { (_, score) -> score }
                                    .chunked(6)
                                    .first()
                            }
                            .flatMap { (_, results) -> results.map { (teamId, _) -> teamId } }
                            .count { teamId -> yearTeams.any { team -> team.id == teamId } }
                    } else 0
                }
                .values.sum()
        }

        return StandingsIntEntry(standardScoring = standardScoringWins, topSixRankings = topSixScoringWins)
    }

    private fun getLossesForMember(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup ->
                    teams.filter { it.year == matchup.year }
                        .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                }
                .count { matchup ->
                    !matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                }.toStandingsIntEntry()
        }

    private fun getPointsScored(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsDoubleEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup ->
                    teams.filter { it.year == matchup.year }
                        .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                }
                .sumOf { matchup ->
                    matchup.getTeamScores(teams.filter { it.year == matchup.year }.map { it.id })?.standardScore ?: 0.0
                }.let { points -> StandingsDoubleEntry(standardScoring = points, topSixRankings = points) }
        }

    private fun getPointsAgainst(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsDoubleEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup ->
                    teams.filter { it.year == matchup.year }
                        .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                }
                .sumOf { matchup ->
                    matchup.getOtherTeamScores(teams.filter { it.year == matchup.year }.map { it.id })?.standardScore
                        ?: 0.0
                }.let { points -> StandingsDoubleEntry(standardScoring = points, topSixRankings = points) }
        }

    private fun getChampionshipCount(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup -> matchup.week == 16 && matchup.playoffTierType == PlayoffTierType.WinnersBracket }
                .filter { matchup ->
                    teams.filter { it.year == matchup.year }
                        .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                }
                .count { matchup ->
                    matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                }.toStandingsIntEntry()
        }

    private fun getPlayoffAppearanceCount(
        matchups: List<Matchup>,
        member: Member,
        teamsMap: Map<Int, List<Team>>
    ): StandingsIntEntry =
        matchups
            .groupBy { it.year }
            .count { (year, matchups) ->
                val team = teamsMap[year]?.firstOrNull { it.owners.contains(member.id) }
                matchups
                    .filter { listOf(it.homeTeamId, it.awayTeamId).contains(team?.id) }
                    .any { matchup ->
                        matchup.playoffTierType == PlayoffTierType.WinnersBracket
                    }
            }.toStandingsIntEntry()

}