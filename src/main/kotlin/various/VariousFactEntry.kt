package dev.mfazio.espnffb.various

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VariousFactEntry(
    val number: Int,
    val title: String,
    val subtitle: String? = null,
)
