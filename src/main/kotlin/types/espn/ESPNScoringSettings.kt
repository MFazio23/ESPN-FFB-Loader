package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNScoringSettings(
    val scoringItems: List<ESPNScoringItem>
)