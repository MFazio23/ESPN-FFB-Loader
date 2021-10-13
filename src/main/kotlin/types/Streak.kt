package dev.mfazio.espnffb.types

data class Streaks(
    val current: Int = 0,
    val currentYear: Int = 0,
    val streaks: List<Streak> = emptyList()
)

data class Streak(
    val teamId: Int,
    val startYear: Int,
    val length: Int = 0,
)
