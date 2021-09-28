package dev.mfazio.espnffb.types.espn

data class ESPNMapValue(
    val ineligible: Boolean,
    val rank: Double,
    val result: Any?,
    val score: Double
)