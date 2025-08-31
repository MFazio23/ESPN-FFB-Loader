package dev.mfazio.espnffb.types

data class SeasonResult(
    val regularSeasonGames: Int,
    val regularSeasonWins: Int,
    val playoffGames: Int,
    val playoffWins: Int,
    val finalSeasonStanding: Int? = null,
    val isChampion: Boolean = false,
) {
    val totalGames = regularSeasonGames + playoffGames
}
