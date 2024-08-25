package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.ESPNConfig.excludedMemberIds
import dev.mfazio.espnffb.ESPNConfig.excludedMemberIdsPerYear
import dev.mfazio.espnffb.types.*

object ESPNStandingsCalculator {

    fun getStandingsFromMatchups(
        matchups: List<Matchup>,
        members: List<Member>,
        teams: List<Team>,
        teamsMap: Map<Int, List<Team>> = emptyMap()
    ): List<Standings> =
        members.filter { !excludedMemberIds.contains(it.id) }.map { member ->
            Standings(
                member = member,
                seasons = getSeasonsForMember(member, teams),
                wins = getWinsForMember(matchups, member, teamsMap),
                losses = getLossesForMember(matchups, member, teams),
                pointsScored = getPointsScored(matchups, member, teams),
                pointsAgainst = getPointsAgainst(matchups, member, teams),
                championships = getChampionshipCount(matchups, member, teams),
                playoffApps = getPlayoffAppearanceCount(matchups, member, teamsMap),
                championshipApps = getChampionshipGameAppearances(matchups, member, teams),
            )
        }

    fun getSeasonsForMember(member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }
            .distinctBy { teams -> teams.year }
            .count()
            .let { count ->
                StandingsIntEntry(count, topSixRankings = count)
            }


    fun getWinsForTeam(team: Team, matchups: List<Matchup>, season: Int? = null): StandingsIntEntry =
        matchups
            .filter { season == null || it.year == season }
            .filter { matchup -> matchup.awayScores.standardScore != 0.0 || matchup.homeScores.standardScore != 0.0 }
            .count { matchup -> matchup.didTeamWin(team.id) }
            .toStandingsIntEntry()

    fun getLossesForTeam(team: Team, matchups: List<Matchup>, season: Int? = null): StandingsIntEntry =
        matchups
            .filter { season == null || it.year == season }
            .filter { matchup -> matchup.awayScores.standardScore != 0.0 || matchup.homeScores.standardScore != 0.0 }
            .count { matchup -> matchup.includesTeam(team.id) && !matchup.didTeamWin(team.id) }
            .toStandingsIntEntry()

    fun getWinsForMember(
        matchups: List<Matchup>,
        member: Member,
        teamsMap: Map<Int, List<Team>>
    ): StandingsIntEntry {

        val memberTeams = teamsMap
            .flatMap { (_, teams) -> teams }
            .filter { it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }

        val standardScoringWins = getStandardScoringWins(memberTeams, matchups)

        val topSixScoringWins = getTopSixScoringWins(member, matchups, teamsMap)

        return StandingsIntEntry(standardScoring = standardScoringWins.size, topSixRankings = topSixScoringWins)
    }

    fun getStandardScoringWins(teams: List<Team>, matchups: List<Matchup>) = matchups
        .filter { matchup ->
            teams
                .filter { it.year == matchup.year }
                .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
        }
        .filter { matchup ->
            matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
        }

    private fun getTopSixScoringWins(
        member: Member,
        matchups: List<Matchup>,
        teamsMap: Map<Int, List<Team>>
    ) = matchups
        .groupBy { it.year }
        .mapValues { (year, matchups) ->
            val yearTeams = teamsMap[year]?.filter { it.owners.contains(member.id) }

            if (yearTeams?.isNotEmpty() == true) {

                matchups
                    .groupBy { it.week }
                    .mapValues { (_, matchups) ->
                        matchups
                            .flatMap {
                                listOf(it.awayTeamId to it.awayScores.standardScore) +
                                        listOf(it.homeTeamId to it.homeScores.standardScore)
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

    private fun getLossesForMember(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }
            .let { teams ->
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
                    teams
                        .filter { it.year == matchup.year && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }
                        .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                }
                .sumOf { matchup ->
                    matchup.getTeamScores(teams.filter { it.year == matchup.year }.map { it.id })?.standardScore ?: 0.0
                }.let { points -> StandingsDoubleEntry(standardScoring = points, topSixRankings = points) }
        }

    private fun getPointsAgainst(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsDoubleEntry =
        teamList
            .filter { it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }
            .let { teams ->
                matchups
                    .filter { matchup ->
                        teams.filter { it.year == matchup.year }
                            .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                    }
                    .sumOf { matchup ->
                        matchup.getOtherTeamScores(teams.filter { it.year == matchup.year }
                            .map { it.id })?.standardScore
                            ?: 0.0
                    }.let { points -> StandingsDoubleEntry(standardScoring = points, topSixRankings = points) }
            }

    fun getChampionshipCount(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }
            .let { teams ->
                matchups
                    .groupBy { "${it.year}-${it.week}" }
                    .values
                    .filter { matchups -> matchups.count { it.playoffTierType == PlayoffTierType.WinnersBracket } == 1 }
                    .map { matchups -> matchups.first { it.playoffTierType == PlayoffTierType.WinnersBracket } }
                    .filter { matchup ->
                        teams
                            .filter { it.year == matchup.year }
                            .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
                    }
                    .count { matchup ->
                        matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                    }.toStandingsIntEntry()
            }

    fun isChampionInYear(year: Int, teamId: Int, matchups: List<Matchup>): Boolean =
        matchups
            .filter { it.year == year }
            .groupBy { it.week }
            .values
            .filter { weekMatchups -> weekMatchups.count { it.playoffTierType == PlayoffTierType.WinnersBracket } == 1 }
            .map { weekMatchups -> weekMatchups.first { it.playoffTierType == PlayoffTierType.WinnersBracket } }
            .any { matchup ->
                matchup.didTeamWin(teamId)
            }

    private fun getChampionshipGameAppearances(
        matchups: List<Matchup>,
        member: Member,
        teamList: List<Team>
    ): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true }
            .let { teams ->
                matchups
                    .groupBy { "${it.year}-${it.week}" }
                    .values
                    .filter { matchups -> matchups.count { it.playoffTierType == PlayoffTierType.WinnersBracket } == 1 }
                    .map { matchups -> matchups.first { it.playoffTierType == PlayoffTierType.WinnersBracket } }
                    .count { matchup ->
                        teams
                            .filter { it.year == matchup.year }
                            .any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId }
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
                val team = teamsMap[year]?.firstOrNull {
                    it.owners.contains(member.id) && excludedMemberIdsPerYear[it.year]?.contains(member.id) != true
                }
                matchups
                    .filter { listOf(it.homeTeamId, it.awayTeamId).contains(team?.id) }
                    .any { matchup ->
                        matchup.playoffTierType == PlayoffTierType.WinnersBracket
                    }
            }.toStandingsIntEntry()

}
