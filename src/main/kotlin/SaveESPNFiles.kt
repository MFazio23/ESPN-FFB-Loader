package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import dev.mfazio.espnffb.handlers.ESPNServiceHandler
import kotlinx.coroutines.delay

suspend fun main() {

    ESPNLocalFileHandler.saveRawDataToFiles(
        startYear = 2022,
        startWeek = 1,
        endWeek = 1,
    )

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val teamsMap = getTeamYearMapFromScoreboards(scoreboards)
    val members = getMemberListFromScoreboards(scoreboards)
    val teams = getTeamListFromScoreboards(scoreboards)

    val matchups = getMatchupsFromScoreboards(scoreboards, teamsMap).filterNotNull()
    ESPNLocalFileHandler.saveMatchups(matchups)

    val recordBook = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups)

    ESPNLocalFileHandler.saveRecordBook(recordBook)

    val standings = ESPNStandingsCalculator.getStandingsFromMatchups(matchups, members, teams, teamsMap)

    ESPNLocalFileHandler.saveStandings(standings)

}