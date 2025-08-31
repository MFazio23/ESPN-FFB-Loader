package dev.mfazio.espnffb.various

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VariousFactCard(
    val title: String,
    val subtitle: String? = null,
    val entries: List<VariousFactEntry>,
)
