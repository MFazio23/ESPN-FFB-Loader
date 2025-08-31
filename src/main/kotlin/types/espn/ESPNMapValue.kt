package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNMapValue(
    val ineligible: Boolean = false,
    val rank: Double? = null,
    val result: Any?,
    val score: Double? = null
)