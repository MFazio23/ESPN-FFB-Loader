package dev.mfazio.espnffb.converters

import dev.mfazio.espnffb.types.*
import dev.mfazio.espnffb.types.espn.ESPNScoreboard

fun getMatchupsFromScoreboards(scoreboards: List<ESPNScoreboard>, allTeams: Map<Int, List<Team>?>): List<Matchup?> =
    scoreboards.flatMap { scoreboard ->
        val teams = allTeams[scoreboard.seasonId] ?: listOf()

        scoreboard.schedule.filter { it.matchupPeriodId == scoreboard.scoringPeriodId }.map { schedule ->
            val homeTeam = teams.find { team -> team.id == schedule.home.teamId } ?: return@map null
            val awayTeam = teams.find { team -> team.id == schedule.away.teamId } ?: return@map null

            val homeRoster = schedule.home.rosterForCurrentScoringPeriod?.entries ?: emptyList()
            val awayRoster = schedule.away.rosterForCurrentScoringPeriod?.entries ?: emptyList()

            val homePlayers = homeRoster.map { Player.fromESPNEntry(it) }
            val awayPlayers = awayRoster.map { Player.fromESPNEntry(it) }

            val homeScore = schedule.home.pointsByScoringPeriod[schedule.matchupPeriodId.toString()] ?: 0.0
            val awayScore = schedule.away.pointsByScoringPeriod[schedule.matchupPeriodId.toString()] ?: 0.0

            Matchup(
                id = schedule.id,
                year = scoreboard.seasonId,
                week = schedule.matchupPeriodId,
                homeTeamId = homeTeam.id,
                awayTeamId = awayTeam.id,
                homeScores = TeamScores(
                    standardScore = homeScore,
                    bestBallScore = getBestBallLineup(homePlayers)?.sumOf { it.points },
                ),
                awayScores = TeamScores(
                    standardScore = awayScore,
                    bestBallScore = getBestBallLineup(awayPlayers)?.sumOf { it.points },
                ),
                isHomeOriginalWinner = schedule.winner == "HOME"
            )
        }
    }


fun getMemberListFromScoreboards(scoreboards: List<ESPNScoreboard>) = scoreboards
    .flatMap { it.members }
    .distinctBy { it.id }
    .map { member ->
        member.copy(id = member.id.replace("{", "").replace("}", ""))
    }

fun getTeamYearMapFromScoreboards(scoreboards: List<ESPNScoreboard>): Map<Int, List<Team>> = scoreboards
    .groupBy { it.seasonId }
    .mapValues { (_, scoreboards) -> getTeamListFromScoreboards(scoreboards) }

fun getTeamListFromScoreboards(scoreboards: List<ESPNScoreboard>): List<Team> {
    val members = getMemberListFromScoreboards(scoreboards)

    return scoreboards.flatMap {
        it.teams
            .map { espnTeam -> Team.fromESPNTeam(espnTeam, members) }
    }.distinctBy { it.id }
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

    return listOf(topPlayer) + listOfNotNull(when (topPlayer.position) {
        Position.RB -> filteredPlayers.filter { it.position != Position.RB }.maxByOrNull { it.points }
        Position.TE -> filteredPlayers.filter { it.position != Position.TE }.maxByOrNull { it.points }
        else -> filteredPlayers.filter { it.id != topPlayer.id }.maxByOrNull { it.points }
    })
}

/*
fun getRecordBookFromMatchups(matchups: List<Matchup>): RecordBook {

    return RecordBook(
        matchups.max
    )
}*/
