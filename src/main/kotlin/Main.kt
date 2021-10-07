package dev.mfazio.espnffb

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.RecordBook
import dev.mfazio.espnffb.types.RecordBookEntry
import dev.mfazio.espnffb.types.Streak
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import kotlin.system.measureTimeMillis

suspend fun main() {

    val endYear = 2020

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()

    val allTeams = getTeamYearMapFromScoreboards(scoreboards)

    ESPNLocalFileHandler.saveMatchups(
        getMatchupsFromScoreboards(scoreboards, allTeams)
    )

    /*val matchups = ESPNLocalFileHandler.loadMatchups().filter { it.year <= endYear }
    val teams = getTeamListFromScoreboards(scoreboards)
    val members = getMemberListFromScoreboards(scoreboards).map(Member::fromESPNMember)

    val standings = ESPNStandingsCalculator.getStandingsFromMatchups(matchups, members, teams)

    standings
        .sortedByDescending { it.championships?.standardScoring }
        .map { standing ->
            "${standing.member.fullName}: ${standing.championships?.standardScoring}"
        }
        .forEach(::println)*/

    val finalWeekOfPlayoffs = scoreboards
        .filter { it.scoringPeriodId >= 15 && it.seasonId == 2017 }
        .map { scoreboard ->
            scoreboard.copy(schedule = scoreboard.schedule.filter {
                it.matchupPeriodId == scoreboard.scoringPeriodId && it.playoffTierType == "LOSERS_CONSOLATION_LADDER"
            })
        }

    val finalWeekMatchups = getMatchupsFromScoreboards(finalWeekOfPlayoffs, allTeams)
        .filterNotNull()
        .map { 

        }

    finalWeekMatchups.forEach(::println)
}