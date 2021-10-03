package dev.mfazio.espnffb

import dev.mfazio.espnffb.converters.getMatchupsFromScoreboards
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import kotlinx.coroutines.delay

suspend fun main() {
    val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()

    ESPNLocalFileHandler.saveTeamList(scoreboards)
}