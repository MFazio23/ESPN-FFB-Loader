package dev.mfazio.espnffb.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import java.io.File

object ESPNLocalServiceHandler {

    private const val dataFolderPath = "C:\\dev\\Files\\espn-ffb\\espn-data\\raw"

    private val moshi = Moshi.Builder().build()

    fun getScoreboardDataForWeek(year: Int, week: Int): ESPNScoreboard? {
        val file = File("$dataFolderPath/$year-$week-scoreboard.json").also {
            if (!it.exists()) return null
        }

        val jsonData = file.readText()

        return if (year >= ESPNConfig.modernStartYear) {
            moshi.adapter(ESPNScoreboard::class.java).fromJson(jsonData)
        } else {
            val type = Types.newParameterizedType(List::class.java, ESPNScoreboard::class.java)
            moshi.adapter<List<ESPNScoreboard>>(type).fromJson(jsonData)?.firstOrNull()
        }
    }
}