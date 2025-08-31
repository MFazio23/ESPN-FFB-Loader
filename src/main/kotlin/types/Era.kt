package dev.mfazio.espnffb.types

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Era(
    @Transient
    val id: String = "",
    val title: String,
    val subtitle: String?,
    val startYear: Int,
    val endYear: Int
)
