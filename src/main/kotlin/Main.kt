package dev.mfazio.espnffb

import dev.mfazio.espnffb.service.ESPNLocalServiceHandler
import dev.mfazio.espnffb.types.*

suspend fun main(args: Array<String>) {

    //val scoreboards = ESPNDataSaver.loadAllLocalScoreboardFiles()

    /*val scoreboard = ESPNLocalServiceHandler.getScoreboardDataForWeek(2020, 3) ?: return

    val weekThreeSchedules = scoreboard.schedule.filter { it.matchupPeriodId == 3 }

    val teams = ESPNLocalServiceHandler.getTeamsForYear(2020)
    val members = ESPNLocalServiceHandler.getMembers()*/

    /*weekThreeSchedules.forEach { espnSchedule ->
        val id = espnSchedule.id
        val periodId = espnSchedule.matchupPeriodId

        val homeId = espnSchedule.home.teamId
        val awayId = espnSchedule.away.teamId

        val homeTeam = teams?.firstOrNull { it.id == homeId }
        val awayTeam = teams?.firstOrNull { it.id == awayId }

        val homeScore = espnSchedule.home.pointsByScoringPeriod["$periodId"]
        val awayScore = espnSchedule.away.pointsByScoringPeriod["$periodId"]

        println("Matchup #$id, week $periodId - ${homeTeam?.fullName} vs. ${awayTeam?.fullName} ($homeScore vs. $awayScore)")
    }*/

    /*val fazScottMatchup = weekThreeSchedules.firstOrNull { it.id == 12 } ?: return

    val homeRoster = fazScottMatchup.home.rosterForCurrentScoringPeriod.entries
    val awayRoster = fazScottMatchup.away.rosterForCurrentScoringPeriod.entries

    val homePlayers = homeRoster.map { Player.fromESPNEntry(it) }
    val awayPlayers = awayRoster.map { Player.fromESPNEntry(it) }

    val homeTeam = teams?.firstOrNull { it.id == fazScottMatchup.home.teamId } ?: return
    val awayTeam = teams?.firstOrNull { it.id == fazScottMatchup.away.teamId } ?: return

    //players.forEach(::println)

    println("Normal = ${homePlayers.filter { it.lineupSlot != LineupSlot.BENCH }.sumOf { it.points }}")
    println("Best ball = ${getBestBallLineup(homePlayers).sumOf { it.points }}")

    val matchup = Matchup(
        id = fazScottMatchup.id,
        year = 2020,
        week = 3,
        home = homeTeam,
        away = awayTeam,
        homeScores = TeamScores(
            standardScore = homePlayers.filter { it.lineupSlot != LineupSlot.BENCH }.sumOf { it.points },
            bestBallScore = getBestBallLineup(homePlayers).sumOf { it.points },
        ),
        awayScores = TeamScores(
            standardScore = awayPlayers.filter { it.lineupSlot != LineupSlot.BENCH }.sumOf { it.points },
            bestBallScore = getBestBallLineup(awayPlayers).sumOf { it.points },
        ),
    )

    println(matchup)*/

    val scoreboard = ESPNLocalServiceHandler.getScoreboardDataForWeek(2020, 1) ?: return

    val kickers = scoreboard.schedule
        .filter { it.matchupPeriodId == 1 }
        .flatMap { schedule ->
            schedule.home.rosterForCurrentScoringPeriod.entries + schedule.away.rosterForCurrentScoringPeriod.entries
        }.filter { it.playerPoolEntry.player.defaultPositionId == 5 }

    val kickerPointMap = kickers.map {
        val entry = it.playerPoolEntry
        val player = entry.player
        player to KickerStats.fromESPNStats(player.stats.firstOrNull { stats -> stats.proTeamId != 0 }?.stats ?: mapOf())
    }

    kickerPointMap.map { (player, stats) ->
        listOf(
            player.fullName,
            stats.underForty,
            stats.underForty + stats.underFortyMissed,
            stats.forty,
            "",
            stats.fiftyPlus,
            "",
            stats.xp,
            stats.xp + stats.xpMissed,
            "",
            stats.getModernScore()
        ).joinToString("\t")
    }.forEach(::println)

    /*val categories = kickerPointMap.flatMap { (_, statsMap) -> statsMap?.keys ?: listOf() }.distinct().sortedBy { it.toInt() }

    val headers = (listOf("Player") + categories).joinToString("\t")
    println(headers)

    println(
        kickerPointMap.joinToString("\n") { (player, statsMap) ->
            val stats = categories.map { category -> (statsMap?.get(category)) }

            (listOf(player.fullName) + stats.map { it?.toString().orEmpty() }).joinToString("\t")
        }
    )*/

}