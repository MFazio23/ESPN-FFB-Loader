package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.*

object ESPNStandingsCalculator {

    fun getStandingsFromMatchups(matchups: List<Matchup>, members: List<Member>, teams: List<Team>): List<Standings> =
        members.filter { !Standings.excludedMemberIds.contains(it.id) }.map { member ->
            Standings(
                member = member,
                seasons = getSeasonsForMember(member, teams),
                wins = getWinsForMember(matchups, member, teams),
                losses = getLossesForMember(matchups, member, teams),
                pointsScored = getPointsScored(matchups, member, teams),
                pointsAgainst = getPointsAgainst(matchups, member, teams),
                championships = getChampionshipCount(matchups, member, teams),
                lastPlaces = null,
                playoffApps = null,
            )
        }

    private fun getSeasonsForMember(member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) }
            .distinctBy { teams -> teams.year }
            .count()
            .let { count ->
                StandingsIntEntry(count)
            }


    private fun getWinsForMember(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup -> teams.filter { it.year == matchup.year }.any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId } }
                .count { matchup ->
                    matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                }.toStandingsIntEntry()
        }

    private fun getLossesForMember(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup -> teams.filter { it.year == matchup.year }.any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId } }
                .count { matchup ->
                    !matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                }.toStandingsIntEntry()
        }

    private fun getPointsScored(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsDoubleEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup -> teams.filter { it.year == matchup.year }.any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId } }
                .sumOf { matchup ->
                    matchup.getTeamScores(teams.filter { it.year == matchup.year }.map { it.id })?.standardScore ?: 0.0
                }.toStandingsDoubleEntry()
        }

    private fun getPointsAgainst(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsDoubleEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup -> teams.filter { it.year == matchup.year }.any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId } }
                .sumOf { matchup ->
                    matchup.getOtherTeamScores(teams.filter { it.year == matchup.year }.map { it.id })?.standardScore ?: 0.0
                }.toStandingsDoubleEntry()
        }

    private fun getChampionshipCount(matchups: List<Matchup>, member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList.filter { it.owners.contains(member.id) }.let { teams ->
            matchups
                .filter { matchup -> matchup.week == 16 && matchup.playoffTierType == PlayoffTierType.WinnersBracket }
                .filter { matchup -> teams.filter { it.year == matchup.year }.any { it.id == matchup.awayTeamId || it.id == matchup.homeTeamId } }
                .count { matchup ->
                    matchup.didTeamWin(teams.filter { it.year == matchup.year }.map { it.id })
                }.toStandingsIntEntry()
        }
}