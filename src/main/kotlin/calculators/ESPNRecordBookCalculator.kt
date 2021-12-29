package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.*
import kotlin.math.abs

object ESPNRecordBookCalculator {

    private const val itemsToInclude = 10

    fun getRecordBookFromMatchups(matchups: List<Matchup>): RecordBook =
        RecordBook(
            mostPointsGame = getMostPointsInGame(matchups),
            mostPointsSeason = getMostPointsInSeason(matchups),
            mostPointsSeasonWithPlayoffs = getMostPointsInSeason(matchups, true),
            mostPointsPerWeek = getMostPointsPerWeek(matchups),
            mostPointsPerWeekWithPlayoffs = getMostPointsPerWeek(matchups, true),
            mostPointsMatchup = getMostPointsInMatchup(matchups),
            fewestPointsGame = getFewestPointsInGame(matchups),
            fewestPointsSeason = getFewestPointsInSeason(matchups),
            fewestPointsSeasonWithPlayoffs = getFewestPointsInSeason(matchups, true),
            fewestPointsPerWeek = getFewestPointsPerWeek(matchups),
            fewestPointsPerWeekWithPlayoffs = getFewestPointsPerWeek(matchups, true),
            fewestPointsMatchup = getFewestPointsInMatchup(matchups),
            smallestMarginOfVictory = getSmallestMarginOfVictory(matchups),
            largestMarginOfVictory = getLargestMarginOfVictory(matchups),
            mostPointsAllowed = getMostPointsAllowed(matchups),
            mostPointsAllowedWithPlayoffs = getMostPointsAllowed(matchups, true),
            mostPointsAllowedPerWeek = getMostPointsAllowedPerWeek(matchups),
            mostPointsAllowedPerWeekWithPlayoffs = getMostPointsAllowedPerWeek(matchups, true),
            fewestPointsAllowed = getFewestPointsAllowed(matchups),
            fewestPointsAllowedWithPlayoffs = getFewestPointsAllowed(matchups, true),
            fewestPointsAllowedPerWeek = getFewestPointsAllowedPerWeek(matchups),
            fewestPointsAllowedPerWeekWithPlayoffs = getFewestPointsAllowedPerWeek(matchups, true),
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

    fun getMostPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        getPointsInSeason(matchups, includePlayoffs)
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
        getPointsInSeason(matchups, includePlayoffs)
            //.filter { (year, _) -> year != ESPNConfig.currentYear }
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

    private fun getMostPointsPerWeek(matchups: List<Matchup>, includePlayoffs: Boolean = false): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsInSeason(matchups, includePlayoffs)
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    val weeklyScore = score / (seasonLengths[year] ?: 1)
                    RecordBookEntry(
                        weeklyScore,
                        mapOf(teamId to weeklyScore),
                        year,
                    )
                }
            }
            .sortedByDescending { it.value }
            .take(itemsToInclude)
    }

    private fun getFewestPointsPerWeek(matchups: List<Matchup>, includePlayoffs: Boolean = false): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsInSeason(matchups, includePlayoffs)
            //.filter { (year, _) -> year != ESPNConfig.currentYear }
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    val weeklyScore = score / (seasonLengths[year] ?: 1)
                    RecordBookEntry(
                        weeklyScore,
                        mapOf(teamId to weeklyScore),
                        year,
                    )
                }
            }
            .sortedBy { it.value }
            .take(itemsToInclude)
    }

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
        getPointsAllowedInSeason(matchups, includePlayoffs)
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

    private fun getMostPointsAllowedPerWeek(matchups: List<Matchup>, includePlayoffs: Boolean = false): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsAllowedInSeason(matchups, includePlayoffs)
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    val weeklyScore = score / (seasonLengths[year] ?: 1)
                    RecordBookEntry(
                        weeklyScore,
                        mapOf(teamId to weeklyScore),
                        year,
                    )
                }
            }
            .sortedByDescending { it.value }
            .take(itemsToInclude)
    }


    private fun getFewestPointsAllowed(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        getPointsAllowedInSeason(matchups, includePlayoffs)
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

    private fun getFewestPointsAllowedPerWeek(matchups: List<Matchup>, includePlayoffs: Boolean = false): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsAllowedInSeason(matchups, includePlayoffs)
            .flatMap { (year, teamScores) ->
                teamScores.map { (teamId, score) ->
                    val weeklyScore = score / (seasonLengths[year] ?: 1)
                    RecordBookEntry(
                        weeklyScore,
                        mapOf(teamId to weeklyScore),
                        year,
                    )
                }
            }
            .sortedBy { it.value }
            .take(itemsToInclude)
    }

    private fun getLongestStreak(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        matchupMapFunction: (matchup: Matchup, homeTeamWon: Boolean) -> List<Pair<Int, StreakItem>>
    ) = getProperMatchups(matchups, includePlayoffs)
        .flatMap { matchup ->
            val homeTeamWon = matchup.homeScores.standardScore > matchup.awayScores.standardScore ||
                (matchup.homeScores.standardScore == matchup.awayScores.standardScore && matchup.isHomeOriginalWinner)
            matchupMapFunction(matchup, homeTeamWon)
        }
        .groupBy { (teamId, _) -> teamId }
        .mapValues { (teamId, streakItems) ->
            streakItems
                .fold(Streaks()) { streaks, (_, streakItem) ->
                    if (streakItem.teamWon) {
                        streaks.copy(
                            current = streaks.current + 1,
                            currentYear = if (streaks.currentYear == 0) streakItem.startYear else streaks.currentYear
                        )
                    } else {
                        val currentStreak = streaks.streaks + if (streaks.current == 0) {
                            emptyList()
                        } else {
                            listOf(Streak(teamId, streaks.currentYear, streaks.current))
                        }
                        streaks.copy(
                            current = 0,
                            currentYear = 0,
                            streaks = currentStreak
                        )
                    }
                }
                .let { streaks ->
                    if (streaks.current > 0) streaks.copy(
                        streaks = streaks.streaks + Streak(teamId, streaks.currentYear, streaks.current)
                    ) else streaks
                }
        }
        .values
        .flatMap { it.streaks }
        .sortedWith(compareBy({ -it.length }, { it.startYear }))
        .take(itemsToInclude)
        .map { (teamId, year, streak) ->
            RecordBookEntry(
                streak.toDouble(),
                mapOf(teamId to streak.toDouble()),
                season = year
            )
        }

    private fun getLongestWinningStreak(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        getLongestStreak(matchups, includePlayoffs) { matchup, homeTeamWon ->
            listOf(
                matchup.homeTeamId to StreakItem(matchup.year, homeTeamWon),
                matchup.awayTeamId to StreakItem(matchup.year, !homeTeamWon),
            )
        }

    private fun getLongestLosingStreak(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        getLongestStreak(matchups, includePlayoffs) { matchup, homeTeamWon ->
            listOf(
                matchup.homeTeamId to StreakItem(matchup.year, !homeTeamWon),
                matchup.awayTeamId to StreakItem(matchup.year, homeTeamWon),
            )
        }

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

    private fun getPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean) =
        getProperMatchups(matchups, includePlayoffs)
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.homeScores) + listOf(matchup.awayTeamId to matchup.awayScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scores.standardScore } }
            }

    private fun getPointsAllowedInSeason(matchups: List<Matchup>, includePlayoffs: Boolean) =
        getProperMatchups(matchups, includePlayoffs)
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.awayScores) + listOf(matchup.awayTeamId to matchup.homeScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scores.standardScore } }
            }

    private fun getProperMatchups(matchups: List<Matchup>, includePlayoffs: Boolean = false): List<Matchup> = matchups
        .groupBy { it.year }
        .mapValues { (_, matchups) ->
            matchups
                .groupBy { it.week }
                .filterValues { weekMatchups -> includePlayoffs || weekMatchups.none { it.playoffTierType != PlayoffTierType.None } }
        }
        .values.flatMap { it.values }.flatten()

    private fun getWeeksForSeasons(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        getProperMatchups(matchups, includePlayoffs)
            .groupBy { it.year }
            .mapValues { (_, yearMatchups) -> yearMatchups.distinctBy { it.week }.size }
}
