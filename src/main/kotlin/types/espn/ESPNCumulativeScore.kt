package dev.mfazio.espnffb.types.espn

data class ESPNCumulativeScore(
    val losses: Int,
    val scoreByStat: ESPNMapValue,
    val statBySlot: Any?,
    val ties: Int,
    val wins: Int
)