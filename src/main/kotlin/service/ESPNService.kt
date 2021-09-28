package dev.mfazio.espnffb.service

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ESPNService {

    // Historical URL: /leagueHistory/358793?scoringPeriodId=5&view=modular&view=mNav&view=mMatchupScore&view=mScoreboard&view=mSettings&view=mTopPerformers&view=mTeam&seasonId=2015
    // Modern URL: /seasons/2020/segments/0/leagues/358793?view=modular&view=mNav&view=mMatchupScore&view=mScoreboard&view=mSettings&view=mTopPerformers&view=mTeam

    @GET("${ESPNConfig.baseURL}leagueHistory/${ESPNConfig.leagueID}")
    suspend fun getHistoricalESPNWeekJson(
        @Query("seasonId") year: Int,
        @Query("scoringPeriodId") week: Int,
        @Query("view") views: List<String> = listOf("mMatchupScore", "mScoreboard", "mTeam")
    ): String

    @GET("${ESPNConfig.baseURL}seasons/{seasonId}/segments/0/leagues/${ESPNConfig.leagueID}")
    suspend fun getModernESPNWeekJson(
        @Path("seasonId") year: Int,
        @Query("scoringPeriodId") week: Int,
        @Query("view") views: List<String> = listOf("mMatchupScore", "mScoreboard", "mTeam")
    ): String

    @GET("${ESPNConfig.baseURL}leagueHistory/${ESPNConfig.leagueID}")
    suspend fun getHistoricalESPNWeek(
        @Query("seasonId") year: Int,
        @Query("scoringPeriodId") week: Int,
        @Query("view") views: List<String> = listOf("mMatchupScore", "mScoreboard", "mTeam")
    ): List<ESPNScoreboard>

    @GET("${ESPNConfig.baseURL}seasons/{seasonId}/segments/0/leagues/${ESPNConfig.leagueID}")
    suspend fun getModernESPNWeek(
        @Path("seasonId") year: Int,
        @Query("scoringPeriodId") week: Int,
        @Query("view") views: List<String> = listOf("mMatchupScore", "mScoreboard", "mTeam")
    ): ESPNScoreboard
}