package dev.mfazio.espnffb.types

data class SeasonTeamMatchupResult(
    val season: Int,
    val weeks: Int,
    val playoffStartWeek: Int,
    val playoffEndWeek: Int,
    val matchupResults: List<TeamMatchupResult>
)
