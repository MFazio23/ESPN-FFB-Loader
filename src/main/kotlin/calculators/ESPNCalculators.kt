package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.KeeperEntry
import dev.mfazio.espnffb.types.KickerStats
import dev.mfazio.espnffb.types.Position
import dev.mfazio.espnffb.types.espn.ESPNPlayerPoolEntry
import dev.mfazio.espnffb.types.espn.ESPNScoreboard

fun ESPNPlayerPoolEntry.getStatTotal(newKickerStats: Boolean) = if (!newKickerStats) this.appliedStatTotal else {
    val playerStats = this.player.stats.firstOrNull()
    if (playerStats != null && this.player.defaultPositionId == Position.K.id) {
        KickerStats.fromESPNStats(playerStats.stats ?: emptyMap()).getModernScore().toDouble()
    } else this.appliedStatTotal
}

fun ESPNScoreboard.getKeeperPrices() = this.teams.flatMap { team ->
    team.roster?.entries?.map { entry ->
        entry.playerPoolEntry.let { playerPoolEntry ->
            KeeperEntry(
                teamId = team.id,
                teamName = team.name ?: "${team.location} ${team.nickname}",
                playerId = playerPoolEntry.player.id,
                playerName = playerPoolEntry.player.fullName,
                playerTeamName = playerPoolEntry.player.getProTeamName() ?: "FA",
                year = this.seasonId,
                position = Position.fromESPNId(playerPoolEntry.player.defaultPositionId),
                keeperPrice = playerPoolEntry.keeperValue,
            )
        }
    } ?: emptyList()
}