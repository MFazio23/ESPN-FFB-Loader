package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNCumulativeScore(
    val losses: Int,
    val scoreByStat: ESPNMapValue? = null,
    val statBySlot: Any?,
    val ties: Int,
    val wins: Int
)