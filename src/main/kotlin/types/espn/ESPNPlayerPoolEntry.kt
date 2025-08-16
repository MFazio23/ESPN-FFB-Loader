package dev.mfazio.espnffb.types.espn

import dev.mfazio.utils.extensions.orZero

data class ESPNPlayerPoolEntry(
    val appliedStatTotal: Double,
    val id: Int,
    val onTeamId: Int,
    val keeperValue: Int = 0,
    val keeperValueFuture: Int? = null,
    val player: ESPNPlayer,
    val status: String
) {
    val projectedStatTotal: Double
        get() = player.stats.firstOrNull { stats ->
            ESPNStatSource.fromId(stats.statSourceId) == ESPNStatSource.Projected
        }?.appliedTotal.orZero()
}