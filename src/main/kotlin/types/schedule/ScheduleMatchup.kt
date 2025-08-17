package dev.mfazio.espnffb.types.schedule

import dev.mfazio.espnffb.types.Team

data class ScheduleMatchup(
    val home: Team,
    val away: Team,
    val matchupType: MatchupType = MatchupType.Regular
) {
    fun contains(team: Team) = home == team || away == team
    fun contains(teamId: Int) = home.id == teamId || away.id == teamId
    fun flipped() = ScheduleMatchup(home = away, away = home, matchupType)

    override fun toString() = "${away.fullName} @ ${home.fullName} ($matchupType)"
}