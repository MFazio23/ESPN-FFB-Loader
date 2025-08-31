package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNScoringItem(
    val isReverseItem: Boolean,
    val points: Double,
    val statId: Int
)