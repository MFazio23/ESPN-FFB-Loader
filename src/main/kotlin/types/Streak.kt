package dev.mfazio.espnffb.types

data class Streak(
    val max: Int = 0,
    val maxYear: Int = 0,
    val current: Int = 0,
    val currentYear: Int = 0
)
