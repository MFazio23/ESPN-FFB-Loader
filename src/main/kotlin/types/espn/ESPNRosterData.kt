package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNRosterData(
    val id: Int,
    val seasonId: Int,
    val members: List<ESPNMember>,
    val teams: List<ESPNTeam>
)
