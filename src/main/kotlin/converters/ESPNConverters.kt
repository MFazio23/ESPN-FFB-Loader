package dev.mfazio.espnffb.converters

import dev.mfazio.espnffb.ESPNConfig.excludedMemberIds
import dev.mfazio.espnffb.ESPNConfig.excludedMemberIdsPerYear
import dev.mfazio.espnffb.ESPNConfig.mappedMemberIds
import dev.mfazio.espnffb.types.*
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.utils.extensions.getOrZero
import dev.mfazio.utils.extensions.orZero

fun getMatchupsFromScoreboards(
    scoreboards: List<ESPNScoreboard>,
    teamsMap: Map<Int, List<Team>?>? = null,
    includePlayers: Boolean = false,
): List<Matchup?> {
    val allTeams = teamsMap ?: getTeamYearMapFromScoreboards(scoreboards)

    return scoreboards.flatMap { scoreboard ->
        val teams = allTeams[scoreboard.seasonId] ?: listOf()

        scoreboard.schedule.filter { it.matchupPeriodId == scoreboard.scoringPeriodId }.mapNotNull { schedule ->
            val homeTeam = teams.find { team -> team.id == schedule.home.teamId } ?: return@mapNotNull null
            val awayTeam = teams.find { team -> team.id == schedule.away.teamId } ?: return@mapNotNull null

            val homeRoster = schedule.home.rosterForCurrentScoringPeriod?.entries ?: emptyList()
            val awayRoster = schedule.away.rosterForCurrentScoringPeriod?.entries ?: emptyList()

            val homePlayers = homeRoster.map { Player.fromESPNEntry(it) }
            val awayPlayers = awayRoster.map { Player.fromESPNEntry(it) }

            val homeScore = schedule.home.pointsByScoringPeriod?.getOrZero(schedule.matchupPeriodId.toString())
            val awayScore = schedule.away.pointsByScoringPeriod?.getOrZero(schedule.matchupPeriodId.toString())

            if (homeScore.orZero() == 0.0 && awayScore.orZero() == 0.0) return@mapNotNull null
            Matchup(
                id = schedule.id,
                year = scoreboard.seasonId,
                week = schedule.matchupPeriodId,
                homeTeamId = homeTeam.id,
                awayTeamId = awayTeam.id,
                homeScores = TeamScores(
                    standardScore = homeScore.orZero(),
                    bestBallScore = getBestBallLineup(homePlayers)?.sumOf { it.points },
                ),
                awayScores = TeamScores(
                    standardScore = awayScore.orZero(),
                    bestBallScore = getBestBallLineup(awayPlayers)?.sumOf { it.points },
                ),
                homePlayers = if (includePlayers) homePlayers else null,
                awayPlayers = if (includePlayers) awayPlayers else null,
                isHomeOriginalWinner = schedule.winner == "HOME",
                playoffTierType = PlayoffTierType.fromESPNString(schedule.playoffTierType)
            )
        }
    }
}


fun getESPNMemberListFromScoreboards(scoreboards: List<ESPNScoreboard>) = scoreboards
    .flatMap { it.members }
    .map { member ->
        val startingMemberId = member.id.replace("{", "").replace("}", "")
        val memberId = mappedMemberIds.getOrDefault(startingMemberId, startingMemberId)
        member.copy(id = memberId)
    }
    .distinctBy { it.id }
    .filter { !excludedMemberIds.contains(it.id) }

fun getMemberListFromScoreboards(scoreboards: List<ESPNScoreboard>) =
    getESPNMemberListFromScoreboards(scoreboards).map(Member::fromESPNMember)

fun getTeamYearMapFromScoreboards(scoreboards: List<ESPNScoreboard>): TeamYearMap = scoreboards
    .groupBy { it.seasonId }
    .mapValues { (seasonId, scoreboards) ->
        getTeamListFromScoreboards(scoreboards, excludedMemberIdsPerYear[seasonId] ?: emptyList())
    }

fun getTeamListFromScoreboards(
    scoreboards: List<ESPNScoreboard>,
    excludedMemberIds: List<String> = emptyList()
): List<Team> {
    val members = getESPNMemberListFromScoreboards(scoreboards)

    return scoreboards.flatMap {
        it.teams.map { espnTeam -> Team.fromESPNTeam(espnTeam, members, it.seasonId, excludedMemberIds) }
    }.distinctBy { "${it.id}-${it.year}" }.sortedBy { it.id }
}


fun getBestBallLineup(players: List<Player>): List<Player>? {
    if (players.size < 10 ||
        players.count { it.position == Position.RB } < 2 ||
        players.count { it.position == Position.WR } < 2
    ) {
        return null
    }

    val qb = players.filter { player -> player.position == Position.QB }.maxByOrNull { player -> player.points }
    val k = players.filter { player -> player.position == Position.K }.maxByOrNull { player -> player.points }
    val def = players.filter { player -> player.position == Position.DEF }.maxByOrNull { player -> player.points }
    val te = players.filter { player -> player.position == Position.TE }.maxByOrNull { player -> player.points }
    val (rb1, rb2) = players
        .filter { player -> player.position == Position.RB }
        .sortedByDescending { it.points }
        .take(2)
    val (wr1, wr2) = players
        .filter { player -> player.position == Position.WR }
        .sortedByDescending { it.points }
        .take(2)
    val (flex1, flex2) = getFlexPositions(
        players.filter {
            !listOf(qb, k, def, te, rb1, rb2, wr1, wr2).contains(it)
        }
    )

    return listOfNotNull(
        qb,
        rb1,
        rb2,
        flex1,
        wr1,
        wr2,
        flex2,
        te,
        def,
        k,
    )
}

fun getFlexPositions(players: List<Player>): List<Player> {
    val filteredPlayers = players.filter {
        listOf(Position.RB, Position.WR, Position.TE).contains(it.position)
    }

    val topPlayer = filteredPlayers.maxByOrNull { it.points } ?: return emptyList()

    val flex = listOf(topPlayer) + listOfNotNull(when (topPlayer.position) {
        Position.RB -> filteredPlayers.filter { it.position != Position.RB }.maxByOrNull { it.points }
        Position.TE -> filteredPlayers.filter { it.position != Position.TE }.maxByOrNull { it.points }
        else -> filteredPlayers.filter { it.id != topPlayer.id }.maxByOrNull { it.points }
    })

    // This makes sure that the RW/WR flex spot is listed first.
    return if (flex.first().position == Position.TE) flex.reversed() else flex
}

fun getCurrentWeekAndYear(matchups: List<Matchup>): Pair<Int, Int> {
    val playedMatchups = matchups.filter { it.homeScores.standardScore > 0.0 && it.awayScores.standardScore > 0.0 }
    val currentYear = playedMatchups.maxOf { it.year }
    val currentWeek = playedMatchups.filter { it.year == currentYear }.maxOf { it.week }

    return currentWeek to currentYear
}