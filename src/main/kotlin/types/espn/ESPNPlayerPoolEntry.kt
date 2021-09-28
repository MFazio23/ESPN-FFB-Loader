package dev.mfazio.espnffb.types.espn

data class ESPNPlayerPoolEntry(
    val appliedStatTotal: Double,
    val id: Int,
    val onTeamId: Int,
    val player: ESPNPlayer,
    val status: String
)