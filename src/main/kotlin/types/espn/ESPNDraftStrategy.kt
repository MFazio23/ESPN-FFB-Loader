package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNDraftStrategy(
    val keeperPlayerIds: List<Int>
)