package dev.mfazio.espnffb.types

data class RecordBook(
    val mostPointsGame: List<RecordBookEntry> = emptyList(),
    val mostPointsSeason: List<RecordBookEntry> = emptyList(),
    val mostPointsSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val mostPointsMatchup: List<RecordBookEntry> = emptyList(),
    val fewestPointsGame: List<RecordBookEntry> = emptyList(),
    val fewestPointsSeason: List<RecordBookEntry> = emptyList(),
    val fewestPointsSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsMatchup: List<RecordBookEntry> = emptyList(),
    val smallestMarginOfVictory: List<RecordBookEntry> = emptyList(),
    val largestMarginOfVictory: List<RecordBookEntry> = emptyList(),
    val mostPointsAllowed: List<RecordBookEntry> = emptyList(),
    val mostPointsAllowedWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsAllowed: List<RecordBookEntry> = emptyList(),
    val fewestPointsAllowedWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val longestWinningStreak: List<RecordBookEntry> = emptyList(),
    val longestWinningStreakWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val longestLosingStreak: List<RecordBookEntry> = emptyList(),
    val longestLosingStreakWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val lowestWinningScore: List<RecordBookEntry> = emptyList(),
    val highestLosingScore: List<RecordBookEntry> = emptyList(),
)
