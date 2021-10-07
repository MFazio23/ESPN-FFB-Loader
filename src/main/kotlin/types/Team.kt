package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.types.espn.ESPNMember
import dev.mfazio.espnffb.types.espn.ESPNTeam

data class Team(
    val id: Int,
    val owners: List<String>,
    val location: String,
    val nickname: String,
    val shortName: String,
    val year: Int,
) {
    val fullName = "$location $nickname"

    companion object {
        fun fromESPNTeam(team: ESPNTeam, members: List<ESPNMember>, year: Int = -1): Team =
            Team(
                id = team.id,
                owners = team.owners.mapNotNull { memberId ->
                    members.firstOrNull { "{${it.id}}" == memberId }?.id
                },
                location = team.location.trim(),
                nickname = team.nickname.trim(),
                shortName = team.abbrev,
                year = year,
            )
    }
}
