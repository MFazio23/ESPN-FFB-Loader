package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.KickerStats
import dev.mfazio.espnffb.types.Player
import dev.mfazio.espnffb.types.Position
import dev.mfazio.espnffb.types.espn.ESPNPlayerPoolEntry

fun ESPNPlayerPoolEntry.getStatTotal(newKickerStats: Boolean) = if (!newKickerStats) this.appliedStatTotal else {
    val playerStats = this.player.stats.firstOrNull()
    if (playerStats != null && this.player.defaultPositionId == Position.K.id) {
        KickerStats.fromESPNStats(playerStats.stats).getModernScore().toDouble()
    } else this.appliedStatTotal
}