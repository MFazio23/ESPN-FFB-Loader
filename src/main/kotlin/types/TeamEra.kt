package dev.mfazio.espnffb.types

data class TeamEra(
    val teamName: String,
    val startYear: Int,
    val endYear: Int,
    val owners: List<String>
)
