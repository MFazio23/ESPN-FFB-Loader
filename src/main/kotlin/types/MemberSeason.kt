package dev.mfazio.espnffb.types

import dev.mfazio.utils.extensions.isNotNullOrEmpty

data class MemberSeason(
    val member: Member,
    override val year: Int,
    val team: Team,
    override val wins: Int,
    override val losses: Int,
    override val isChampion: Boolean = false,
    val coOwners: String? = null,
) : Season {
    override val id: String
        get() = member.id
    override val eraId: String
        get() = "${member.id}-${team.fullName}-${coOwners}"
    override val title: String
        get() = team.fullName
    override val subtitle: String?
        get() = if (coOwners.isNotNullOrEmpty()) "w/ $coOwners" else null
}
