package dev.mfazio.espnffb.types.espn

data class ESPNScoringItem(
    val isReverseItem: Boolean,
    val points: Double,
    val statId: Int
)