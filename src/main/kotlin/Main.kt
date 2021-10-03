package dev.mfazio.espnffb

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.calculators.ESPNRecordBookCalculator
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.handlers.ESPNLocalServiceHandler
import dev.mfazio.espnffb.types.RecordBook
import dev.mfazio.espnffb.types.RecordBookEntry
import dev.mfazio.espnffb.types.Streak
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import kotlin.system.measureTimeMillis

suspend fun main() {

    val matchups = ESPNLocalFileHandler.loadMatchups()

    val recordBook = ESPNRecordBookCalculator.getRecordBookFromMatchups(matchups)


    ESPNLocalFileHandler.saveRecordBook(recordBook)

    /*val data = listOf(
        true,
        true,
        true,
        false,
        false,
        true,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        false,
        true,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
    )

    var longestStreak = 0
    var currentStreak = 0

    data.forEach { result ->
        if (!result) {
            currentStreak = 0
        } else {
            currentStreak++
        }
        longestStreak = maxOf(longestStreak, currentStreak)
    }

    println("Longest streak: ${longestStreak}W")

    val streak = data.fold(Streak()) { acc, result ->
        if (result) {
            Streak(
                maxOf(acc.max, acc.current + 1),
                acc.current + 1
            )
        } else {
            Streak(acc.max, 0)
        }
    }

    println("Longest Streak (fold): ${streak.max}W")*/

}