package dev.mfazio.espnffb.handlers

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.converters.getESPNMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.types.*
import dev.mfazio.espnffb.types.espn.ESPNMember
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
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
                val jsonResult = loadJsonResultForWeek(year, week, year >= ESPNConfig.modernStartYear)

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
        val members = getESPNMemberListFromScoreboards(scoreboards)

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

    fun saveRecordBooks(recordBooks: RecordBooks) {
        File("$dataFolderPath/record-book.json").writeText(
            moshi.adapter(RecordBooks::class.java).toJson(recordBooks)
        )
    }

    fun saveStandings(standings: List<Standings>) {
        val type = Types.newParameterizedType(List::class.java, Standings::class.java)

        File("$dataFolderPath/standings.json").writeText(
            moshi.adapter<List<Standings>>(type).toJson(standings)
        )
    }

    fun saveMemberVsTeamRecords(memberRecordMap: Map<Member, Map<Team, TeamRecord>>) {
        val teamRecordMapType = Types.newParameterizedType(Map::class.java, Integer::class.java, TeamRecord::class.java)
        val type = Types.newParameterizedType(Map::class.java, String::class.java, teamRecordMapType)

        val outputMemberRecordMap = memberRecordMap
            .mapKeys { (member, _) -> member.id }
            .mapValues { (_, teamRecordMap) -> teamRecordMap.mapKeys { (team, _) -> team.id } }

        val adapter = Moshi.Builder().build().adapter<Map<String, Map<Int, TeamRecord>>>(type)

        File("$dataFolderPath/member-vs-team-records.json").writeText(
            adapter.toJson(outputMemberRecordMap)
        )
    }

    fun saveTeamSummaries(teamSummaries: List<TeamMemberSummary>) {
        val teamSeasonListType = Types.newParameterizedType(List::class.java, TeamMemberSummary::class.java)

        val adapter = Moshi.Builder().build().adapter<List<TeamMemberSummary>>(teamSeasonListType)

        File("$dataFolderPath/team-summaries.json").writeText(
            adapter.toJson(teamSummaries)
        )
    }

    fun saveOwnerSummaries(ownerSummaries: List<TeamMemberSummary>) {
        val teamSeasonListType = Types.newParameterizedType(List::class.java, TeamMemberSummary::class.java)

        val adapter = Moshi.Builder().build().adapter<List<TeamMemberSummary>>(teamSeasonListType)

        File("$dataFolderPath/owner-summaries.json").writeText(
            adapter.toJson(ownerSummaries)
        )
    }
}
