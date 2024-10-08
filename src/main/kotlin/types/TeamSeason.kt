package dev.mfazio.espnffb.types

data class TeamSeason(
    override val year: Int,
    val team: Team,
    val owners: List<Member>,
    override val wins: Int,
    override val losses: Int,
    override val isChampion: Boolean = false,
) : Season {
    override val id: String
        get() = team.id.toString()
    override val eraId: String
        get() = (listOf(team.fullName) + owners.map { it.fullName }).joinToString("-")
    override val title: String
        get() = team.fullName
    override val subtitle: String?
        get() = owners.joinToString(", ") { it.fullName }
}
