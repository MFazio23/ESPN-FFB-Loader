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

    val matchups = ESPNLocalFileHandler.loadMatchups()

    ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups).longestWinningStreak.forEach(::println)

}

data class Streaks(
    val current: Int = 0,
    val streaks: List<Int> = emptyList(),
)