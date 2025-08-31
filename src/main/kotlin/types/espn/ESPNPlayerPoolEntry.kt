package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass
import dev.mfazio.utils.extensions.orZero

@JsonClass(generateAdapter = true)
data class ESPNPlayerPoolEntry(
    val appliedStatTotal: Double,
    val id: Int,
    val onTeamId: Int,
    val keeperValue: Int = 0,
    val keeperValueFuture: Int? = null,
    val player: ESPNPlayer,
    val status: String? = null
) {
    val projectedStatTotal: Double
        get() = player.stats.firstOrNull { stats ->
            ESPNStatSource.fromId(stats.statSourceId) == ESPNStatSource.Projected
        }?.appliedTotal.orZero()
}