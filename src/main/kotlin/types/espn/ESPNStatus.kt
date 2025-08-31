package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNStatus(
    val activatedDate: Long,
    val createdAsLeagueType: Int,
    val currentLeagueType: Int,
    val currentMatchupPeriod: Int,
    val finalScoringPeriod: Int,
    val firstScoringPeriod: Int,
    val isActive: Boolean,
    val isExpired: Boolean,
    val isFull: Boolean,
    val isPlayoffMatchupEdited: Boolean,
    val isToBeDeleted: Boolean,
    val isViewable: Boolean,
    val isWaiverOrderEdited: Boolean,
    val latestScoringPeriod: Int,
    val previousSeasons: List<Int>,
    val standingsUpdateDate: Long? = null,
    val teamsJoined: Int,
    val transactionScoringPeriod: Int,
    val waiverLastExecutionDate: Long? = null,
    val waiverProcessStatus: Map<String, Int>
)