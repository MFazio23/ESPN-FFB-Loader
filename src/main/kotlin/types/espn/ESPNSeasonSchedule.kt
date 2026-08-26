package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNSeasonSchedule(
    val id: Int,
    val seasonId: Int,
    val schedule: List<ESPNBasicSchedule>,
    val teams: List<ESPNTeam>,
)

@JsonClass(generateAdapter = true)
data class ESPNBasicSchedule(
    val id: Int,
    val matchupPeriodId: Int,
    val away: ESPNBasicScheduleTeam,
    val home: ESPNBasicScheduleTeam,
)

@JsonClass(generateAdapter = true)
data class ESPNBasicScheduleTeam(
    val teamId: Int,
)


