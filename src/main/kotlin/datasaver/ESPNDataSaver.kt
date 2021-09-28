package dev.mfazio.espnffb.datasaver

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.service.ESPNLocalServiceHandler
import dev.mfazio.espnffb.service.ESPNServiceHandler
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.types.espn.ESPNMember
import kotlinx.coroutines.delay
import java.io.File

object ESPNDataSaver {
    private const val baseFolderPath = "c:/dev/Files/espn-ffb/"
    private const val dataFolderPath = "${baseFolderPath}espn-data"
    private const val rawDataFolderPath = "$dataFolderPath/raw"

    private val moshi = Moshi.Builder().build()

    fun loadAllLocalScoreboardFiles(): List<ESPNScoreboard> =
        (ESPNConfig.historicalStartYear..ESPNConfig.modernEndYear).flatMap { year ->
            (ESPNConfig.startWeek..ESPNConfig.endWeek).mapNotNull { week ->
                ESPNLocalServiceHandler.getScoreboardDataForWeek(year, week)
            }
        }

    suspend fun saveRawDataToFiles() {
        createDataFolderAsNeeded(rawDataFolderPath)

        (ESPNConfig.historicalStartYear..ESPNConfig.historicalEndYear).forEach { year ->
            (ESPNConfig.startWeek..ESPNConfig.endWeek).forEach { week ->
                val jsonResult = loadJsonResultForWeek(year, week)

                saveJsonResultForWeek(year, week, jsonResult)

                delay(500)
            }
        }

        (ESPNConfig.modernStartYear..ESPNConfig.modernEndYear).forEach { year ->
            (ESPNConfig.startWeek..ESPNConfig.endWeek).forEach { week ->
                val jsonResult = loadJsonResultForWeek(year, week, true)

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

    fun getMemberListFromScoreboards(scoreboards: List<ESPNScoreboard>) = scoreboards
        .flatMap { it.members }
        .distinctBy { it.id }
        .map { member ->
            member.copy(id = member.id.replace("{", "").replace("}", ""))
        }

    fun getTeamYearMapFromScoreboards(scoreboards: List<ESPNScoreboard>): Map<Int, List<Team>> {
        val members = getMemberListFromScoreboards(scoreboards)

        return scoreboards
            .groupBy { it.seasonId }
            .mapValues { (_, scoreboards) ->
                scoreboards.flatMap {
                    it.teams
                        .map { espnTeam -> Team.fromESPNTeam(espnTeam, members) }
                }.distinctBy { it.id }
            }
    }
}