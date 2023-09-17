package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.types.RecordBooks

suspend fun main() {

    ESPNLocalFileHandler.saveRawDataToFiles(
        startYear = 2022,
        startWeek = 3,
        endWeek = 3,
    )

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val teamsMap = getTeamYearMapFromScoreboards(scoreboards)
    val members = getMemberListFromScoreboards(scoreboards)
    val teams = getTeamListFromScoreboards(scoreboards)

    ESPNLocalFileHandler.saveTeamYearMap(scoreboards)
    ESPNLocalFileHandler.saveMemberList(scoreboards)

    val matchups = getMatchupsFromScoreboards(scoreboards, teamsMap).filterNotNull()
    ESPNLocalFileHandler.saveMatchups(matchups)

    val recordBooks = RecordBooks(
        standard = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups),
        modern = ESPNRecordBookCalculator.getModernRecordBook(matchups),
        bestBall = ESPNRecordBookCalculator.getBestBallRecordBook(matchups),
    )
    ESPNLocalFileHandler.saveRecordBooks(recordBooks)

    val standings = ESPNStandingsCalculator.getStandingsFromMatchups(matchups, members, teams, teamsMap)
    ESPNLocalFileHandler.saveStandings(standings)

    ESPNLocalFileHandler.saveTeamYearMap(scoreboards)
    ESPNLocalFileHandler.saveMemberList(scoreboards)

}