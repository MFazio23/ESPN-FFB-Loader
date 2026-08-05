package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.types.espn.ESPNEntry

data class Player(
    val id: Int,
    val name: String,
    val position: Position,
    val points: Double,
    val projectedPoints: Double? = null,
    val lineupSlot: LineupSlot,
) {
    companion object {
        fun fromESPNEntry(espnEntry: ESPNEntry): Player {
            val ppEntry = espnEntry.playerPoolEntry
            val espnPlayer = ppEntry.player

            return Player(
                id = espnEntry.playerId,
                name = espnPlayer.fullName,
                position = Position.fromESPNId(espnPlayer.defaultPositionId),
                points = ppEntry.appliedStatTotal,
                projectedPoints = ppEntry.projectedStatTotal,
                lineupSlot = LineupSlot.fromESPNId(espnEntry.lineupSlotId)
            )
        }
    }
}

fun List<Player>?.getProjectedScore() = if (this.isNullOrEmpty()) null else {
    this.sumOf { player ->
        if (player.lineupSlot.isStarter()) player.projectedPoints ?: 0.0 else 0.0
    }
}