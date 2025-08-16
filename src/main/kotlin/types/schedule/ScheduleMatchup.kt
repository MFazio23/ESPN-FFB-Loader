package dev.mfazio.espnffb.types.schedule

import dev.mfazio.espnffb.types.Team

data class ScheduleMatchup(
    val home: Team,
    val away: Team,
    val matchupType: MatchupType = MatchupType.Regular
) {
    fun contains(team: Team) = home == team || away == team
    fun contains(teamId: Int) = home.id == teamId || away.id == teamId
    fun flipped() = ScheduleMatchup(away, home, matchupType)
}