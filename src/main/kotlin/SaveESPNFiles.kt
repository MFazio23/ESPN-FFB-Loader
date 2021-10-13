package dev.mfazio.espnffb

import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.extensions.printEach
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import dev.mfazio.espnffb.handlers.ESPNServiceHandler
import kotlinx.coroutines.delay

suspend fun main() {

    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()
    val teamsMap = getTeamYearMapFromScoreboards(scoreboards)

    val matchups = getMatchupsFromScoreboards(scoreboards).filterNotNull()

    val recordBook = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups)

    ESPNLocalFileHandler.saveRecordBook(recordBook)

}