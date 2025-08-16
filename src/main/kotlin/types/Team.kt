package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.types.espn.ESPNMember
import dev.mfazio.espnffb.types.espn.ESPNTeam

data class Team(
    val id: Int,
    val owners: List<String>,
    val name: String? = null,
    val location: String?,
    val nickname: String?,
    val shortName: String,
    val year: Int,
) {
    val fullName = name ?: "$location $nickname"

    override fun equals(other: Any?): Boolean = other is Team && other.id == id

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + year
        result = 31 * result + owners.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (nickname?.hashCode() ?: 0)
        result = 31 * result + shortName.hashCode()
        result = 31 * result + fullName.hashCode()
        return result
    }

    companion object {
        fun fromESPNTeam(
            team: ESPNTeam,
            members: List<ESPNMember>,
            year: Int = -1,
            excludedMemberIds: List<String>
        ): Team =
            Team(
                id = team.id,
                owners = team.owners.mapNotNull { memberId ->
                    members.filter { !excludedMemberIds.contains(it.id) }.firstOrNull { "{${it.id}}" == memberId }?.id
                },
                name = team.name?.trim(),
                location = team.location?.trim(),
                nickname = team.nickname?.trim(),
                shortName = team.abbrev,
                year = year,
            )
    }
}
