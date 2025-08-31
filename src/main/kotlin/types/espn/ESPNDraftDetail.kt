package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNDraftDetail(
    val drafted: Boolean,
    val inProgress: Boolean
)