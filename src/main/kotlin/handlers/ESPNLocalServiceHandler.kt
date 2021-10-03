package dev.mfazio.espnffb.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import java.io.File

object ESPNLocalServiceHandler {

    private const val dataFolderPath = "/Users/mfazio23/Development/Files/espn-ffb/espn-data"
    private const val rawDataFolderPath = "$dataFolderPath/raw"

    private val moshi = Moshi.Builder().build()

    fun getScoreboardDataForWeek(year: Int, week: Int): ESPNScoreboard? {
        val file = File("$rawDataFolderPath/$year-$week-scoreboard.json").also {
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

    fun getAllTeams(): Map<Int, List<Team>?> =
        (ESPNConfig.historicalStartYear..ESPNConfig.modernEndYear).associateWith { year -> getTeamsForYear(year) }
    
    fun getTeamsForYear(year: Int): List<Team>? {
        val jsonData = File("$dataFolderPath/team-year-map.json").readText()

        val listType = Types.newParameterizedType(List::class.java, Team::class.java)
        val mapType = Types.newParameterizedType(Map::class.java, Integer::class.java, listType)
        val teamYearMap = moshi.adapter<Map<Int, List<Team>>>(mapType).fromJson(jsonData)

        return teamYearMap?.get(year)
    }

    fun getMembers(): List<Member>? {
        val jsonData = File("$dataFolderPath/member-list.json").readText()

        val listType = Types.newParameterizedType(List::class.java, Member::class.java)
        return moshi.adapter<List<Member>>(listType).fromJson(jsonData)
    }
}