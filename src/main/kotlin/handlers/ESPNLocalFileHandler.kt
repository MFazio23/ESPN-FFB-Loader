package dev.mfazio.espnffb.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.RecordBook
import dev.mfazio.espnffb.types.RecordBookEntry
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.types.espn.ESPNMember
import kotlinx.coroutines.delay
import java.io.File

object ESPNLocalFileHandler {
    private val dataFolderPath = "${ESPNConfig.baseFileDirectory}espn-data"
    private val rawDataFolderPath = "$dataFolderPath/raw"

    private val moshi = Moshi.Builder().build()

    fun loadAllLocalScoreboardFiles(): List<ESPNScoreboard> =
        (ESPNConfig.historicalStartYear..ESPNConfig.modernEndYear).flatMap { year ->
            (ESPNConfig.startWeek..ESPNConfig.endWeek).mapNotNull { week ->
                ESPNLocalServiceHandler.getScoreboardDataForWeek(year, week)
            }
        }

    suspend fun saveRawDataToFiles(
        startYear: Int = ESPNConfig.historicalStartYear,
        endYear: Int = ESPNConfig.modernEndYear,
        startWeek: Int = ESPNConfig.startWeek,
        endWeek: Int = ESPNConfig.endWeek,
    ) {
        createDataFolderAsNeeded(rawDataFolderPath)

        (startYear..endYear).forEach { year ->
            (startWeek..endWeek).forEach { week ->
                val jsonResult = loadJsonResultForWeek(year, week, year >= 2019)

                saveJsonResultForWeek(year, week, jsonResult)

                delay(500)
            }
        }
    }

    private fun createDataFolderAsNeeded(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            File(path).mkdirs()
        }
    }

    suspend fun loadJsonResultForWeek(year: Int, week: Int, isModern: Boolean = false): String = if (isModern) {
        ESPNServiceHandler.getModernDataJson(year, week)
    } else ESPNServiceHandler.getHistoricalDataJson(year, week)

    suspend fun saveJsonResultForWeek(year: Int, week: Int, data: String) {
        val filePath = "$rawDataFolderPath/$year-$week-scoreboard.json"

        println("Saving JSON result to $filePath")

        File(filePath).apply {
            this.createNewFile()
            this.writeText(data)
        }
    }

    fun saveMemberList(scoreboards: List<ESPNScoreboard>): List<ESPNMember> {
        val members = getMemberListFromScoreboards(scoreboards)

        val type = Types.newParameterizedType(List::class.java, ESPNMember::class.java)
        val adapter = moshi.adapter<List<ESPNMember>>(type)

        File("$dataFolderPath/member-list.json").writeText(
            adapter.toJson(members)
        )

        return members
    }

    fun saveTeamYearMap(scoreboards: List<ESPNScoreboard>): Map<Int, List<Team>> {
        val teams = getTeamYearMapFromScoreboards(scoreboards)

        val teamListType = Types.newParameterizedType(List::class.java, Team::class.java)
        val type = Types.newParameterizedType(Map::class.java, Integer::class.java, teamListType)
        val adapter = Moshi.Builder().build().adapter<Map<Int, List<Team>>>(type)

        File("$dataFolderPath/team-year-map.json").writeText(
            adapter.toJson(teams)
        )

        return teams
    }

    fun saveTeamList(scoreboards: List<ESPNScoreboard>): List<Team> {
        val teams = getTeamListFromScoreboards(scoreboards)

        val teamListType = Types.newParameterizedType(List::class.java, Team::class.java)
        val adapter = Moshi.Builder().build().adapter<List<Team>>(teamListType)

        File("$dataFolderPath/team-list.json").writeText(
            adapter.toJson(teams)
        )

        return teams
    }

    fun saveMatchups(matchups: List<Matchup?>) {
        val type = Types.newParameterizedType(List::class.java, Matchup::class.java)
        moshi.adapter<List<Matchup>>(type).toJson(matchups.filterNotNull()).let { matchupJson ->
            File("$dataFolderPath/matchups.json").writeText(
                matchupJson
            )
        }
    }

    fun loadMatchups(): List<Matchup> {
        val type = Types.newParameterizedType(List::class.java, Matchup::class.java)

        val matchupJson = File("$dataFolderPath/matchups.json").readText()

        return moshi
            .adapter<List<Matchup>>(type)
            .fromJson(matchupJson)
            ?: emptyList()
    }

    fun saveRecordBook(recordBook: RecordBook) {
        File("$dataFolderPath/record-book.json").writeText(
            moshi.adapter(RecordBook::class.java).toJson(recordBook)
        )
    }
}