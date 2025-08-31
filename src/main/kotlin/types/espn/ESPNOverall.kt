package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNOverall(
    val gamesBack: Double,
    val losses: Int,
    val percentage: Double,
    val pointsAgainst: Double,
    val pointsFor: Double,
    val streakLength: Int,
    val streakType: String,
    val ties: Int,
    val wins: Int
)