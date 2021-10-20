package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.extensions.printEach
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler

suspend fun main() {

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val teamsMap = getTeamYearMapFromScoreboards(scoreboards)
    val matchups = getMatchupsFromScoreboards(scoreboards, teamsMap).filterNotNull()
    val members = getMemberListFromScoreboards(scoreboards)
    val teams = getTeamListFromScoreboards(scoreboards)

    val standings = ESPNStandingsCalculator.getStandingsFromMatchups(matchups, members, teams)

    standings.printEach()

}