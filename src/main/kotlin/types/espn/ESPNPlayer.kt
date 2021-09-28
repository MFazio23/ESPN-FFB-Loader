package dev.mfazio.espnffb.types.espn

data class ESPNPlayer(
    val active: Boolean,
    val defaultPositionId: Int,
    val eligibleDateByPosition: Any,
    val eligibleSlots: List<Int>,
    val firstName: String,
    val fullName: String,
    val id: Int,
    val injured: Boolean,
    val injuryStatus: String,
    val jersey: String,
    val lastName: String,
    val proTeamId: Int,
    val stats: List<ESPNStat>,
    val universeId: Int
)