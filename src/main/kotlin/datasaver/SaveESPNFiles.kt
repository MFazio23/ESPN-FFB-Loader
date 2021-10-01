package dev.mfazio.espnffb.datasaver

import dev.mfazio.espnffb.ESPNConfig
import kotlinx.coroutines.delay

suspend fun main() {
    val year = 2018
    (ESPNConfig.startWeek..ESPNConfig.endWeek).forEach { week ->
        val jsonResult = ESPNDataSaver.loadJsonResultForWeek(year, week, true)

        ESPNDataSaver.saveJsonResultForWeek(year, week, jsonResult)
        delay(500)
    }
}