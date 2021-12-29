package dev.mfazio.espnffb.types

data class Standings(
    val member: Member,
    val seasons: StandingsIntEntry? = null,
    val wins: StandingsIntEntry? = null,
    val losses: StandingsIntEntry? = null,
    val pointsScored: StandingsDoubleEntry? = null,
    val pointsAgainst: StandingsDoubleEntry? = null,
    val championships: StandingsIntEntry? = null,
    val playoffApps: StandingsIntEntry? = null,
    val championshipApps: StandingsIntEntry? = null,
) {
    companion object {
        val excludedMemberIds = listOf(
            "047A1C53-72E7-4173-87EB-88EF5E4BAF7B" // Emily
        )
    }
}
