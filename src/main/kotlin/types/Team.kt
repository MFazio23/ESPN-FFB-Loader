package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.types.espn.ESPNMember

data class Team(
    val id: Int,
    val owners: List<String>,
    val location: String,
    val nickname: String,
    val shortName: String,
) {
    val fullName = "$location $nickname"

    companion object {
        fun fromESPNTeam(team: dev.mfazio.espnffb.types.espn.ESPNTeam, members: List<ESPNMember>): Team =
            Team(
                id = team.id,
                owners = team.owners.mapNotNull { memberId ->
                    members.firstOrNull { "{${it.id}}" == memberId }?.id
                },
                location = team.location,
                nickname = team.nickname,
                shortName = team.abbrev,
            )
    }
}
