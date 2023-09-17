package dev.mfazio.espnffb.types.espn

data class ESPNTeam(
    val abbrev: String,
    val currentProjectedRank: Int,
    val divisionId: Int,
    val draftDayProjectedRank: Int,
    val draftStrategy: ESPNDraftStrategy,
    val id: Int,
    val isActive: Boolean,
    val location: String,
    val logo: String,
    val logoType: String,
    val name: String? = null,
    val nickname: String,
    val owners: List<String>,
    val playoffSeed: Int,
    val points: Double,
    val pointsAdjusted: Double,
    val pointsDelta: Double,
    val primaryOwner: String,
    val rankCalculatedFinal: Int,
    val rankFinal: Int,
    val record: ESPNRecord,
    val tradeBlock: ESPNTradeBlock,
    val transactionCounter: ESPNTransactionCounter,
    val valuesByStat: Any,
    val waiverRank: Int
)