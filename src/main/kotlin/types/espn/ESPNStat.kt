package dev.mfazio.espnffb.types.espn

data class ESPNStat(
    val appliedStats: Any,
    val appliedTotal: Double,
    val appliedTotalCeiling: Double,
    val externalId: String,
    val id: String,
    val proTeamId: Int,
    val scoringPeriodId: Int,
    val seasonId: Int,
    val statSourceId: Int,
    val statSplitTypeId: Int,
    val stats: Any,
    val variance: Any
)