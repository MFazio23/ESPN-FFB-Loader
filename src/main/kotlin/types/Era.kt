package dev.mfazio.espnffb.types

data class Era(
    @Transient
    val id: String,
    val title: String,
    val subtitle: String?,
    val startYear: Int,
    val endYear: Int
)
