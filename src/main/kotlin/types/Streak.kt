package dev.mfazio.espnffb.types

data class Streaks(
    val current: Int = 0,
    val startWeek: Int = 0,
    val startYear: Int = 0,
    val currentWeek: Int = 0,
    val currentYear: Int = 0,
    val streaks: List<Streak> = emptyList()
)

data class Streak(
    val teamId: Int,
    val startWeek: Int,
    val startYear: Int,
    val endWeek: Int,
    val endYear: Int,
    val length: Int = 0,
)
