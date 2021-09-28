package dev.mfazio.espnffb.types.espn

data class ESPNEntry(
    val injuryStatus: String,
    val lineupSlotId: Int,
    val playerId: Int,
    val playerPoolEntry: ESPNPlayerPoolEntry,
    val status: String
)