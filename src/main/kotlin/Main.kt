package dev.mfazio.espnffb

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.extensions.printEach
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import dev.mfazio.espnffb.types.*
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import kotlin.system.measureTimeMillis

suspend fun main() {

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val matchups = ESPNLocalFileHandler.loadMatchups()
    val teamMap = getTeamYearMapFromScoreboards(scoreboards)

    ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups).longestWinningStreakWithPlayoffs.forEach { entry ->
        val team = teamMap[entry.season]?.firstOrNull { it.id == entry.recordHolders.keys.firstOrNull() }

        println("${team?.fullName} - ${entry.value} (${entry.season})")
    }

    /*val lists = matchups
        .filter { it.week <= 13 }
        .flatMap { matchup ->
            val homeTeamWon = matchup.homeScores.standardScore > matchup.awayScores.standardScore ||
                    (matchup.homeScores.standardScore == matchup.awayScores.standardScore && matchup.isHomeOriginalWinner)
            listOf(
                matchup.homeTeamId to StreakItem(matchup.year, homeTeamWon),
                matchup.awayTeamId to StreakItem(matchup.year, !homeTeamWon)
            )
        }
        .groupBy { (teamId, _) -> teamId }
        .mapValues { (teamId, streakItems) ->
            streakItems
                .fold(Streaks()) { streaks, (_, streakItem) ->
                    if (streakItem.teamWon) {
                        streaks.copy(
                            current = streaks.current + 1,
                            currentYear = if (streaks.currentYear == 0) streakItem.startYear else streaks.currentYear
                        )
                    } else {
                        val currentStreak = streaks.streaks + if(streaks.current == 0) {
                            emptyList()
                        } else {
                            listOf(Streak(teamId, streaks.currentYear, streaks.current))
                        }
                        streaks.copy(
                            current = 0,
                            currentYear = 0,
                            streaks = currentStreak
                        )
                    }
                }
        }
        .values
        .flatMap { it.streaks }
        .sortedByDescending { it.length }
        .take(15)*/

    //lists.printEach()


}