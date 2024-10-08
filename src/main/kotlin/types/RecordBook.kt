package dev.mfazio.espnffb.types

data class RecordBook(
    val mostPointsGame: List<RecordBookEntry> = emptyList(),
    val mostPointsGameInPlayoffs: List<RecordBookEntry> = emptyList(),
    val mostPointsSeason: List<RecordBookEntry> = emptyList(),
    val mostPointsSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val mostPointsPerWeek: List<RecordBookEntry> = emptyList(),
    val mostPointsPerWeekWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val mostPointsMatchup: List<RecordBookEntry> = emptyList(),
    val fewestPointsGame: List<RecordBookEntry> = emptyList(),
    val fewestPointsGameInPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsSeason: List<RecordBookEntry> = emptyList(),
    val fewestPointsSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsPerWeek: List<RecordBookEntry> = emptyList(),
    val fewestPointsPerWeekWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsMatchup: List<RecordBookEntry> = emptyList(),
    val smallestMarginOfVictory: List<RecordBookEntry> = emptyList(),
    val largestMarginOfVictory: List<RecordBookEntry> = emptyList(),
    val mostPointsAllowed: List<RecordBookEntry> = emptyList(),
    val mostPointsAllowedWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val mostPointsAllowedPerWeek: List<RecordBookEntry> = emptyList(),
    val mostPointsAllowedPerWeekWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsAllowed: List<RecordBookEntry> = emptyList(),
    val fewestPointsAllowedWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsAllowedPerWeek: List<RecordBookEntry> = emptyList(),
    val fewestPointsAllowedPerWeekWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val longestWinningStreak: List<RecordBookEntry> = emptyList(),
    val longestWinningStreakWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val longestLosingStreak: List<RecordBookEntry> = emptyList(),
    val longestLosingStreakWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val lowestWinningScore: List<RecordBookEntry> = emptyList(),
    val highestLosingScore: List<RecordBookEntry> = emptyList(),
    val mostPointsMissed: List<RecordBookEntry> = emptyList(),
    val mostPointsMissedInSeason: List<RecordBookEntry> = emptyList(),
    val mostPointsMissedInSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val highestPointsPlayedPctInSeason: List<RecordBookEntry> = emptyList(),
    val highestPointsPlayedPctInSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val fewestPointsMissedInSeason: List<RecordBookEntry> = emptyList(),
    val fewestPointsMissedInSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val lowestPointsPlayedPctInSeason: List<RecordBookEntry> = emptyList(),
    val lowestPointsPlayedPctInSeasonWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val highestPointsPlus: List<RecordBookEntry> = emptyList(),
    val highestPointsPlusWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val lowestPointsPlus: List<RecordBookEntry> = emptyList(),
    val lowestPointsPlusWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val highestPointsAllowedPlus: List<RecordBookEntry> = emptyList(),
    val highestPointsAllowedPlusWithPlayoffs: List<RecordBookEntry> = emptyList(),
    val lowestPointsAllowedPlus: List<RecordBookEntry> = emptyList(),
    val lowestPointsAllowedPlusWithPlayoffs: List<RecordBookEntry> = emptyList(),
)
