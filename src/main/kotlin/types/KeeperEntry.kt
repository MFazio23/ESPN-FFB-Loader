package dev.mfazio.espnffb.types

import com.squareup.moshi.JsonClass
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
data class KeeperEntry(
    val teamId: Int,
    val teamName: String,
    val playerId: Int,
    val playerName: String,
    val playerTeamName: String,
    val year: Int,
    val position: Position,
    val keeperPrice: Int,
    val newKeeperPrice: Int = (keeperPrice.toDouble() * 1.5).roundToInt().let {
        if (it < 5) 5 else it
    }
)
