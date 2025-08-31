package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNEntry(
    val injuryStatus: String,
    val lineupSlotId: Int,
    val playerId: Int,
    val playerPoolEntry: ESPNPlayerPoolEntry,
    val status: String
)