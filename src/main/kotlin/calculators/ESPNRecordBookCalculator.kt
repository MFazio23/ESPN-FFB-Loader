package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.*
import kotlin.math.abs

object ESPNRecordBookCalculator {

    private const val itemsToInclude = 10

    fun getRecordBookFromMatchups(matchups: List<Matchup>): RecordBook =
        RecordBook(
            mostPointsGame = getMostPointsInGame(matchups),
            mostPointsSeason = getMostPointsInSeason(matchups),
            mostPointsSeasonWithPlayoffs = getMostPointsInSeason(matchups, true),
            mostPointsMatchup = getMostPointsInMatchup(matchups),
            fewestPointsGame = getFewestPointsInGame(matchups),
            fewestPointsSeason = getFewestPointsInSeason(matchups),
            fewestPointsSeasonWithPlayoffs = getFewestPointsInSeason(matchups, true),
            fewestPointsMatchup = getFewestPointsInMatchup(matchups),
            smallestMarginOfVictory = getSmallestMarginOfVictory(matchups),
            largestMarginOfVictory = getLargestMarginOfVictory(matchups),
            mostPointsAllowed = getMostPointsAllowed(matchups),
            mostPointsAllowedWithPlayoffs = getMostPointsAllowed(matchups, true),
            fewestPointsAllowed = getFewestPointsAllowed(matchups),
            fewestPointsAllowedWithPlayoffs = getFewestPointsAllowed(matchups, true),
            longestWinningStreak = getLongestWinningStreak(matchups),
            longestWinningStreakWithPlayoffs = getLongestWinningStreak(matchups, true),
            longestLosingStreak = getLongestLosingStreak(matchups),
            longestLosingStreakWithPlayoffs = getLongestLosingStreak(matchups, true),
            lowestWinningScore = getLowestWinningScore(matchups),
            highestLosingScore = getHighestLosingScore(matchups),
        )

    private fun getMostPointsInGame(matchups: List<Matchup>) =
        matchups
            .flatMap { matchup ->
                listOf(
                    RecordBookEntry(
                        matchup.homeScores.standardScore,
                        mapOf(matchup.homeTeamId to matchup.homeScores.standardScore),
                        matchup.year,
                        matchup.week,
                    ),
                    RecordBookEntry(
                        matchup.awayScores.standardScore,
                        mapOf(matchup.awayTeamId to matchup.awayScores.standardScore),
                        matchup.year,
                        matchup.week,
                    ),
                )
            }
            .sortedByDescending { entry -> entry.value }
            .take(itemsToInclude)

    private fun getFewestPointsInGame(matchups: List<Matchup>) =
        matchups
            .flatMap { matchup ->
                listOf(
                    RecordBookEntry(
                        matchup.homeScores.standardScore,
                        mapOf(matchup.homeTeamId to matchup.homeScores.standardScore),
                        matchup.year,
                        matchup.week,
                    ),
                    RecordBookEntry(
                        matchup.awayScores.standardScore,
                        mapOf(matchup.awayTeamId to matchup.awayScores.standardScore),
                        matchup.year,
                        matchup.week,
                    ),
                )
            }
            .sortedBy { entry -> entry.value }
            .take(itemsToInclude)

    private fun getMostPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        matchups
            .filter { it.week <= 13 || includePlayoffs }
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.homeScores) + listOf(matchup.awayTeamId to matchup.awayScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scores.standardScore } }
            }
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    RecordBookEntry(
                        score,
                        mapOf(teamId to score),
                        year,
                    )
                }
            }
            .sortedByDescending { it.value }
            .take(itemsToInclude)

    private fun getFewestPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        matchups
            .filter { it.week <= 13 || includePlayoffs }
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.homeScores) + listOf(matchup.awayTeamId to matchup.awayScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scores.standardScore } }
            }
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    RecordBookEntry(
                        score,
                        mapOf(teamId to score),
                        year,
                    )
                }
            }
            .sortedBy { it.value }
            .take(itemsToInclude)

    private fun getMostPointsInMatchup(matchups: List<Matchup>) =
        matchups.sortedByDescending { it.homeScores.standardScore + it.awayScores.standardScore }.map { matchup ->
            RecordBookEntry(
                matchup.homeScores.standardScore + matchup.awayScores.standardScore,
                mapOf(
                    matchup.homeTeamId to matchup.homeScores.standardScore,
                    matchup.awayTeamId to matchup.awayScores.standardScore
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getFewestPointsInMatchup(matchups: List<Matchup>) =
        matchups.sortedBy { it.homeScores.standardScore + it.awayScores.standardScore }.map { matchup ->
            RecordBookEntry(
                matchup.homeScores.standardScore + matchup.awayScores.standardScore,
                mapOf(
                    matchup.homeTeamId to matchup.homeScores.standardScore,
                    matchup.awayTeamId to matchup.awayScores.standardScore
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getSmallestMarginOfVictory(matchups: List<Matchup>) =
        matchups.sortedBy { abs(it.homeScores.standardScore - it.awayScores.standardScore) }.map { matchup ->
            RecordBookEntry(
                abs(matchup.homeScores.standardScore - matchup.awayScores.standardScore),
                mapOf(
                    matchup.homeTeamId to matchup.homeScores.standardScore,
                    matchup.awayTeamId to matchup.awayScores.standardScore
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getLargestMarginOfVictory(matchups: List<Matchup>) =
        matchups.sortedByDescending { abs(it.homeScores.standardScore - it.awayScores.standardScore) }.map { matchup ->
            RecordBookEntry(
                abs(matchup.homeScores.standardScore - matchup.awayScores.standardScore),
                mapOf(
                    matchup.homeTeamId to matchup.homeScores.standardScore,
                    matchup.awayTeamId to matchup.awayScores.standardScore
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getMostPointsAllowed(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        matchups
            .filter { it.week <= 13 || includePlayoffs }
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.awayScores) + listOf(matchup.awayTeamId to matchup.homeScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scores.standardScore } }
            }
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    RecordBookEntry(
                        score,
                        mapOf(teamId to score),
                        year,
                    )
                }
            }
            .sortedByDescending { it.value }
            .take(itemsToInclude)

    private fun getFewestPointsAllowed(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        matchups
            .filter { it.week <= 13 || includePlayoffs }
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.awayScores) + listOf(matchup.awayTeamId to matchup.homeScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scores.standardScore } }
            }
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    RecordBookEntry(
                        score,
                        mapOf(teamId to score),
                        year,
                    )
                }
            }
            .sortedBy { it.value }
            .take(itemsToInclude)

    private fun getLongestWinningStreak(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        matchups
            .filter { it.week <= 13 || includePlayoffs }
            .flatMap { matchup ->
                val homeTeamWon = matchup.homeScores.standardScore > matchup.awayScores.standardScore ||
                    (matchup.homeScores.standardScore == matchup.awayScores.standardScore && matchup.isHomeOriginalWinner)
                listOf(
                    matchup.homeTeamId to StreakItem(matchup.year, homeTeamWon),
                    matchup.awayTeamId to StreakItem(matchup.year, !homeTeamWon)
                )
            }
            .groupBy { (teamId, _) -> teamId }
            .mapValues { (_, results) ->
                results
                    .map { (_, won) -> won }
                    .fold(Streak()) { streak, result ->
                        if (result.teamWon) {
                            Streak(
                                maxOf(streak.max, streak.current + 1),
                                if (streak.current >= streak.max) result.startYear else streak.maxYear,
                                streak.current + 1,
                                if (streak.currentYear == 0) result.startYear else streak.currentYear
                            )
                        } else {
                            Streak(streak.max, streak.maxYear)
                        }
                    }
            }
            .map { (teamId, streak) ->
                RecordBookEntry(
                    streak.max.toDouble(),
                    mapOf(teamId to streak.max.toDouble()),
                    season = streak.maxYear
                )
            }
            .sortedByDescending { it.value }
            .take(itemsToInclude)

    private fun getLongestLosingStreak(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        matchups
            .filter { it.week <= 13 || includePlayoffs }
            .flatMap { matchup ->
                val homeTeamWon = matchup.homeScores.standardScore > matchup.awayScores.standardScore ||
                    (matchup.homeScores.standardScore == matchup.awayScores.standardScore && matchup.isHomeOriginalWinner)
                listOf(
                    matchup.homeTeamId to StreakItem(matchup.year, homeTeamWon),
                    matchup.awayTeamId to StreakItem(matchup.year, !homeTeamWon)
                )
            }
            .groupBy { (teamId, _) -> teamId }
            .mapValues { (_, results) ->
                results
                    .map { (_, won) -> won }
                    .fold(Streak()) { streak, result ->
                        if (!result.teamWon) {
                            Streak(
                                maxOf(streak.max, streak.current + 1),
                                if (streak.current >= streak.max) result.startYear else streak.maxYear,
                                streak.current + 1,
                                if (streak.currentYear == 0) result.startYear else streak.currentYear
                            )
                        } else {
                            Streak(streak.max, streak.maxYear)
                        }
                    }
            }
            .map { (teamId, streak) ->
                RecordBookEntry(
                    streak.max.toDouble(),
                    mapOf(teamId to streak.max.toDouble()),
                    season = streak.maxYear
                )
            }
            .sortedByDescending { it.value }
            .take(itemsToInclude)

    private fun getLowestWinningScore(matchups: List<Matchup>) =
        matchups.sortedBy { maxOf(it.homeScores.standardScore, it.awayScores.standardScore) }.map { matchup ->
            RecordBookEntry(
                maxOf(matchup.homeScores.standardScore, matchup.awayScores.standardScore),
                mapOf(
                    matchup.homeTeamId to matchup.homeScores.standardScore,
                    matchup.awayTeamId to matchup.awayScores.standardScore
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getHighestLosingScore(matchups: List<Matchup>) =
        matchups.sortedByDescending { minOf(it.homeScores.standardScore, it.awayScores.standardScore) }.map { matchup ->
            RecordBookEntry(
                minOf(matchup.homeScores.standardScore, matchup.awayScores.standardScore),
                mapOf(
                    matchup.homeTeamId to matchup.homeScores.standardScore,
                    matchup.awayTeamId to matchup.awayScores.standardScore
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)
}