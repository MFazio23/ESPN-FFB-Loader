package dev.mfazio.espnffb.types.espn

data class ESPNScoreboard(
    val draftDetail: ESPNDraftDetail,
    val gameId: Int,
    val id: Int,
    val members: List<ESPNMember>,
    val schedule: List<ESPNSchedule>,
    val scoringPeriodId: Int,
    val seasonId: Int,
    val segmentId: Int,
    val settings: ESPNSettings,
    val status: ESPNStatus,
    val teams: List<ESPNTeam>
)