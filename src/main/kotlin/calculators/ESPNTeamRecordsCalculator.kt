package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.*
import dev.mfazio.utils.extensions.safeDivide

object ESPNTeamRecordsCalculator {
    fun getAllMemberRecordsFromMatchups(
        members: List<Member>,
        teamList: List<Team>,
        teamMap: Map<Int, List<Team>>,
        matchups: List<Matchup>
    ): Map<Member, Map<Team, TeamRecord>> = members.associateWith { member ->
        teamList.sortedByDescending { it.year }.distinctBy { it.id }.associateWith { opposingTeam ->
            val validMatchups = matchups.filter { matchup ->
                val memberTeam = teamMap[matchup.year]?.find { it.owners.contains(member.id) }
                val relevantTeams = listOfNotNull(opposingTeam.id, memberTeam?.id)
                relevantTeams.contains(matchup.awayTeamId) && relevantTeams.contains(matchup.homeTeamId)
            }
            val playoffMatchups = validMatchups.filter { it.playoffTierType != PlayoffTierType.None }

            val wins = getWinsForMemberAgainstTeam(member, teamMap, validMatchups)
            val playoffWins = getWinsForMemberAgainstTeam(
                member,
                teamMap,
                playoffMatchups
            )

            val (memberMatchupPoints, opponentMatchupPoints) =
                getPointsFromMatchups(member, teamMap, validMatchups, standardScoreFunc)
            val (memberPlayoffMatchupPoints, opponentPlayoffMatchupPoints) =
                getPointsFromMatchups(member, teamMap, playoffMatchups, standardScoreFunc)

            TeamRecord(
                games = validMatchups.size,
                wins = wins,
                losses = validMatchups.size - wins,
                playoffWins = playoffWins,
                playoffLosses = playoffMatchups.size - playoffWins,
                pointsFor = memberMatchupPoints,
                pointsAgainst = opponentMatchupPoints,
                playoffPointsFor = memberPlayoffMatchupPoints,
                playoffPointsAgainst = opponentPlayoffMatchupPoints,
                averagePointsFor = memberMatchupPoints.safeDivide(validMatchups.size.toDouble()),
                averagePointsAgainst = opponentMatchupPoints.safeDivide(validMatchups.size.toDouble()),
                averagePlayoffPointsFor = memberPlayoffMatchupPoints.safeDivide(playoffMatchups.size.toDouble()),
                averagePlayoffPointsAgainst = opponentPlayoffMatchupPoints.safeDivide(playoffMatchups.size.toDouble()),
            )
        }.filter { (_, teamRecord) -> teamRecord.games > 0 }
    }

    fun getFranchiseSeasonsFromMatchups(
        members: List<Member>,
        teamList: List<Team>,
        matchups: List<Matchup>
    ): Map<Team, List<TeamSeason>> = teamList
        .groupBy { it.id }
        .mapKeys { (teamId, _) -> teamList.last { it.id == teamId } }
        .mapValues { (_, teams) ->
            teams.map { team ->
                val owners = members.filter { it.id in team.owners }
                TeamSeason(
                    year = team.year,
                    team = team,
                    owners = owners,
                    wins = ESPNStandingsCalculator.getWinsForTeam(team, matchups, team.year).standardScoring,
                    losses = ESPNStandingsCalculator.getLossesForTeam(team, matchups, team.year).standardScoring,
                    isChampion = ESPNStandingsCalculator.isChampionInYear(team.year, team.id, matchups),
                )
            }
        }

    fun getOwnerSeasonsFromMatchups(
        members: List<Member>,
        teamList: List<Team>,
        matchups: List<Matchup>,
    ): Map<Member, List<MemberSeason>> = members.associateWith { member ->
        teamList.filter { team -> team.owners.contains(member.id) }
            .map { team ->
                val coOwners = team.owners
                    .filter { ownerId -> ownerId != member.id }
                    .map { ownerId -> members.firstOrNull { it.id == ownerId } }
                MemberSeason(
                    member = member,
                    year = team.year,
                    team = team,
                    wins = ESPNStandingsCalculator.getWinsForTeam(team, matchups, team.year).standardScoring,
                    losses = ESPNStandingsCalculator.getLossesForTeam(team, matchups, team.year).standardScoring,
                    isChampion = ESPNStandingsCalculator.isChampionInYear(team.year, team.id, matchups),
                    coOwners = coOwners.mapNotNull { it?.fullName }.joinToString(", ")
                )
            }
    }

    private fun getPointsFromMatchups(
        member: Member,
        teamMap: Map<Int, List<Team>>,
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double,
    ): Pair<Double, Double> = matchups
        .map { matchup ->
            val memberTeam = teamMap[matchup.year]?.find { it.owners.contains(member.id) }

            if (matchup.homeTeamId == memberTeam?.id) {
                scoreFunction(matchup.homeScores) to scoreFunction(matchup.awayScores)
            } else {
                scoreFunction(matchup.awayScores) to scoreFunction(matchup.homeScores)
            }
        }.reduceOrNull { (memberTotal, opponentTotal), (memberScore, opponentScore) ->
            memberTotal + memberScore to opponentTotal + opponentScore
        } ?: (0.0 to 0.0)

    private fun getWinsForMemberAgainstTeam(
        member: Member,
        teamMap: Map<Int, List<Team>>,
        matchups: List<Matchup>
    ): Int = matchups.count { matchup ->
        val memberTeam = teamMap[matchup.year]?.find { it.owners.contains(member.id) } ?: return@count false
        matchup.didTeamWin(memberTeam.id)
    }

    private fun getSeasonsForMember(member: Member, teamList: List<Team>): StandingsIntEntry =
        teamList
            .filter { it.owners.contains(member.id) }
            .distinctBy { teams -> teams.year }
            .count()
            .let { count ->
                StandingsIntEntry(count, topSixRankings = count)
            }
}
