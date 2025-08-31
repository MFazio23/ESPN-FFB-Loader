package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass
import dev.mfazio.espnffb.ESPNConfig.mappedMemberIds

@JsonClass(generateAdapter = true)
data class ESPNTeam(
    val abbrev: String,
    val currentProjectedRank: Int,
    val divisionId: Int,
    val draftDayProjectedRank: Int,
    val draftStrategy: ESPNDraftStrategy? = null,
    val id: Int,
    val isActive: Boolean,
    val location: String?,
    val logo: String? = null,
    val logoType: String? = null,
    val name: String? = null,
    val nickname: String?,
    val owners: List<String>,
    val playoffSeed: Int,
    val points: Double,
    val pointsAdjusted: Double,
    val pointsDelta: Double,
    val primaryOwner: String,
    val rankCalculatedFinal: Int,
    val rankFinal: Int,
    val record: ESPNRecord,
    val roster: ESPNRosterForMatchupPeriod? = null,
    val tradeBlock: ESPNTradeBlock? = null,
    val transactionCounter: ESPNTransactionCounter? = null,
    val valuesByStat: Any? = null,
    val waiverRank: Int
) {
    val ownerId = owners.first().let { id ->
        mappedMemberIds.getOrDefault(id, id)
    }
}