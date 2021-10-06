package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import kotlinx.coroutines.delay

suspend fun main() {
    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()

    val teamYearMap = getTeamYearMapFromScoreboards(scoreboards)

    val matchups = getMatchupsFromScoreboards(scoreboards, teamYearMap).filterNotNull()

    ESPNLocalFileHandler.saveMatchups(matchups)

    val recordBook = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups)

    ESPNLocalFileHandler.saveRecordBook(recordBook)


}