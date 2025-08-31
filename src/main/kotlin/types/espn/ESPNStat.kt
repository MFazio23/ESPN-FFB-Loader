package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNStat(
    val appliedStats: Map<String, Double>? = emptyMap(),
    val appliedTotal: Double?,
    val appliedTotalCeiling: Double?,
    val externalId: String?,
    val id: String?,
    val proTeamId: Int?,
    val scoringPeriodId: Int?,
    val seasonId: Int?,
    val statSourceId: Int?,
    val statSplitTypeId: Int?,
    val stats: Map<String, Double>? = emptyMap(),
    val variance: Map<String, Double>? = emptyMap(),
)