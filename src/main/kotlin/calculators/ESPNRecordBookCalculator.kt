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

    fun getModernRecordBook(allMatchups: List<Matchup>): RecordBook =
        getRecordBookFromMatchups(allMatchups.afterIncrease())

    fun getBestBallRecordBook(allMatchups: List<Matchup>): RecordBook =
        allMatchups.afterIncrease().let { matchups ->
            RecordBook(
                mostPointsGame = getMostPointsInGame(matchups, bestBallScoreFunc),
                mostPointsSeason = getMostPointsInSeason(matchups, useBestBall = true),
                mostPointsSeasonWithPlayoffs = getMostPointsInSeason(matchups, includePlayoffs = true, useBestBall = true),
                mostPointsPerWeek = getMostPointsPerWeek(matchups, useBestBall = true),
                mostPointsPerWeekWithPlayoffs = getMostPointsPerWeek(matchups, includePlayoffs = true, useBestBall = true),
                mostPointsMatchup = getMostPointsInMatchup(matchups, bestBallScoreFunc),
                fewestPointsGame = getFewestPointsInGame(matchups),
                fewestPointsSeason = getFewestPointsInSeason(matchups, useBestBall = true),
                fewestPointsSeasonWithPlayoffs = getFewestPointsInSeason(matchups, includePlayoffs = true, useBestBall = true),
                fewestPointsPerWeek = getFewestPointsPerWeek(matchups, useBestBall = true),
                fewestPointsPerWeekWithPlayoffs = getFewestPointsPerWeek(matchups, includePlayoffs = true, useBestBall = true),
                fewestPointsMatchup = getFewestPointsInMatchup(matchups, bestBallScoreFunc),
                smallestMarginOfVictory = getSmallestMarginOfVictory(matchups, bestBallScoreFunc),
                largestMarginOfVictory = getLargestMarginOfVictory(matchups, bestBallScoreFunc),
                mostPointsAllowed = getMostPointsAllowed(matchups, scoreFunction = bestBallScoreFunc),
                mostPointsAllowedWithPlayoffs = getMostPointsAllowed(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                mostPointsAllowedPerWeek = getMostPointsAllowedPerWeek(matchups, scoreFunction = bestBallScoreFunc),
                mostPointsAllowedPerWeekWithPlayoffs = getMostPointsAllowedPerWeek(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                fewestPointsAllowed = getFewestPointsAllowed(matchups, scoreFunction = bestBallScoreFunc),
                fewestPointsAllowedWithPlayoffs = getFewestPointsAllowed(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                fewestPointsAllowedPerWeek = getFewestPointsAllowedPerWeek(matchups, scoreFunction = bestBallScoreFunc),
                fewestPointsAllowedPerWeekWithPlayoffs = getFewestPointsAllowedPerWeek(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                longestWinningStreak = getLongestWinningStreak(matchups, scoreFunction = bestBallScoreFunc),
                longestWinningStreakWithPlayoffs = getLongestWinningStreak(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                longestLosingStreak = getLongestLosingStreak(matchups, scoreFunction = bestBallScoreFunc),
                longestLosingStreakWithPlayoffs = getLongestLosingStreak(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                lowestWinningScore = getLowestWinningScore(matchups, bestBallScoreFunc),
                highestLosingScore = getHighestLosingScore(matchups, bestBallScoreFunc),
                mostPointsMissed = getMostPointsMissed(matchups),
            )
        }

    private fun getMostPointsInGame(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = matchups
        .flatMap { matchup ->
            listOf(
                RecordBookEntry(
                    scoreFunction(matchup.homeScores),
                    mapOf(matchup.homeTeamId to scoreFunction(matchup.homeScores)),
                    matchup.year,
                    matchup.week,
                ),
                RecordBookEntry(
                    scoreFunction(matchup.awayScores),
                    mapOf(matchup.awayTeamId to scoreFunction(matchup.awayScores)),
                    matchup.year,
                    matchup.week,
                ),
            )
        }
        .sortedByDescending { entry -> entry.value }
        .take(itemsToInclude)

    private fun getFewestPointsInGame(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = matchups
        .flatMap { matchup ->
            listOf(
                RecordBookEntry(
                    scoreFunction(matchup.homeScores),
                    mapOf(matchup.homeTeamId to scoreFunction(matchup.homeScores)),
                    matchup.year,
                    matchup.week,
                ),
                RecordBookEntry(
                    scoreFunction(matchup.awayScores),
                    mapOf(matchup.awayTeamId to scoreFunction(matchup.awayScores)),
                    matchup.year,
                    matchup.week,
                ),
            )
        }
        .sortedBy { entry -> entry.value }
        .take(itemsToInclude)

    private fun getMostPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean = false, useBestBall: Boolean = false) =
        getPointsInSeason(matchups, includePlayoffs, useBestBall)
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

    private fun getFewestPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean = false, useBestBall: Boolean = false) =
        getPointsInSeason(matchups, includePlayoffs, useBestBall)
            .filter { (year, _) -> year != ESPNConfig.currentYear }
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

    private fun getMostPointsPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        useBestBall: Boolean = false
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsInSeason(matchups, includePlayoffs, useBestBall)
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

    private fun getFewestPointsPerWeek(matchups: List<Matchup>, includePlayoffs: Boolean = false, useBestBall: Boolean = false): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsInSeason(matchups, includePlayoffs, useBestBall)
            .filter { (year, _) -> year != ESPNConfig.currentYear }
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

    private fun getMostPointsInMatchup(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) =
        matchups.sortedByDescending { scoreFunction(it.homeScores) + scoreFunction(it.awayScores) }.map { matchup ->
            RecordBookEntry(
                scoreFunction(matchup.homeScores) + scoreFunction(matchup.awayScores),
                mapOf(
                    matchup.homeTeamId to scoreFunction(matchup.homeScores),
                    matchup.awayTeamId to scoreFunction(matchup.awayScores)
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getFewestPointsInMatchup(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) =
        matchups
            .filter { matchup -> matchup.year != ESPNConfig.currentYear }
            .sortedBy { scoreFunction(it.homeScores) + scoreFunction(it.awayScores) }.map { matchup ->
                RecordBookEntry(
                    scoreFunction(matchup.homeScores) + scoreFunction(matchup.awayScores),
                    mapOf(
                        matchup.homeTeamId to scoreFunction(matchup.homeScores),
                        matchup.awayTeamId to scoreFunction(matchup.awayScores)
                    ),
                    matchup.year,
                    matchup.week,
                )
            }.take(itemsToInclude)

    private fun getSmallestMarginOfVictory(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) =
        matchups.sortedBy { abs(scoreFunction(it.homeScores) - scoreFunction(it.awayScores)) }.map { matchup ->
            RecordBookEntry(
                abs(scoreFunction(matchup.homeScores) - scoreFunction(matchup.awayScores)),
                mapOf(
                    matchup.homeTeamId to scoreFunction(matchup.homeScores),
                    matchup.awayTeamId to scoreFunction(matchup.awayScores)
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getLargestMarginOfVictory(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = matchups.sortedByDescending { abs(scoreFunction(it.homeScores) - scoreFunction(it.awayScores)) }.map { matchup ->
        RecordBookEntry(
            abs(scoreFunction(matchup.homeScores) - scoreFunction(matchup.awayScores)),
            mapOf(
                matchup.homeTeamId to scoreFunction(matchup.homeScores),
                matchup.awayTeamId to scoreFunction(matchup.awayScores)
            ),
            matchup.year,
            matchup.week,
        )
    }.take(itemsToInclude)

    private fun getMostPointsAllowed(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsAllowedInSeason(matchups, includePlayoffs, scoreFunction)
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

    private fun getMostPointsAllowedPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsAllowedInSeason(matchups, includePlayoffs, scoreFunction)
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


    private fun getFewestPointsAllowed(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsAllowedInSeason(matchups, includePlayoffs, scoreFunction)
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

    private fun getFewestPointsAllowedPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsAllowedInSeason(matchups, includePlayoffs, scoreFunction)
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
        scoreFunction: (TeamScores) -> Double,
        matchupMapFunction: (matchup: Matchup, homeTeamWon: Boolean) -> List<Pair<Int, StreakItem>>
    ) = getProperMatchups(matchups, includePlayoffs)
        .flatMap { matchup ->
            val homeTeamWon = scoreFunction(matchup.homeScores) > scoreFunction(matchup.awayScores) ||
                (scoreFunction(matchup.homeScores) == scoreFunction(matchup.awayScores) && matchup.isHomeOriginalWinner)
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
                season = year,
                intValue = true,
            )
        }

    private fun getLongestWinningStreak(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getLongestStreak(matchups, includePlayoffs, scoreFunction) { matchup, homeTeamWon ->
        listOf(
            matchup.homeTeamId to StreakItem(matchup.year, homeTeamWon),
            matchup.awayTeamId to StreakItem(matchup.year, !homeTeamWon),
        )
    }

    private fun getLongestLosingStreak(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getLongestStreak(matchups, includePlayoffs, scoreFunction) { matchup, homeTeamWon ->
        listOf(
            matchup.homeTeamId to StreakItem(matchup.year, !homeTeamWon),
            matchup.awayTeamId to StreakItem(matchup.year, homeTeamWon),
        )
    }

    private fun getLowestWinningScore(matchups: List<Matchup>, scoreFunction: (TeamScores) -> Double = standardScoreFunc) =
        matchups.sortedBy { maxOf(scoreFunction(it.homeScores), scoreFunction(it.awayScores)) }.map { matchup ->
            RecordBookEntry(
                maxOf(scoreFunction(matchup.homeScores), scoreFunction(matchup.awayScores)),
                mapOf(
                    matchup.homeTeamId to scoreFunction(matchup.homeScores),
                    matchup.awayTeamId to scoreFunction(matchup.awayScores)
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getHighestLosingScore(matchups: List<Matchup>, scoreFunction: (TeamScores) -> Double = standardScoreFunc) =
        matchups.sortedByDescending { minOf(scoreFunction(it.homeScores), scoreFunction(it.awayScores)) }.map { matchup ->
            RecordBookEntry(
                minOf(scoreFunction(matchup.homeScores), scoreFunction(matchup.awayScores)),
                mapOf(
                    matchup.homeTeamId to scoreFunction(matchup.homeScores),
                    matchup.awayTeamId to scoreFunction(matchup.awayScores)
                ),
                matchup.year,
                matchup.week,
            )
        }.take(itemsToInclude)

    private fun getPointsInSeason(matchups: List<Matchup>, includePlayoffs: Boolean, useBestBall: Boolean = false) =
        getProperMatchups(matchups, includePlayoffs)
            .filter { matchup -> matchup.year != ESPNConfig.currentYear }
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.homeScores) + listOf(matchup.awayTeamId to matchup.awayScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) ->
                        teamScores.sumOf { (_, scores) ->
                            (if (useBestBall) scores.bestBallScore else null) ?: scores.standardScore
                        }
                    }
            }

    private fun getPointsAllowedInSeason(matchups: List<Matchup>, includePlayoffs: Boolean, scoreFunction: (TeamScores) -> Double = standardScoreFunc) =
        getProperMatchups(matchups, includePlayoffs)
            .filter { matchup -> matchup.year != ESPNConfig.currentYear }
            .groupBy { it.year }
            .mapValues { (_, matchups) ->
                matchups
                    .flatMap { matchup -> listOf(matchup.homeTeamId to matchup.awayScores) + listOf(matchup.awayTeamId to matchup.homeScores) }
                    .groupBy { (teamId, _) -> teamId }
                    .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scoreFunction(scores) } }
            }

    private fun getMostPointsMissed(matchups: List<Matchup>) =
        matchups
            .flatMap { matchup ->
                listOf(
                    RecordBookEntry(
                        matchup.homeScores.getBestBallGap(),
                        mapOf(matchup.homeTeamId to matchup.homeScores.getBestBallGap()),
                        matchup.year,
                        matchup.week,
                    ),
                    RecordBookEntry(
                        matchup.awayScores.getBestBallGap(),
                        mapOf(matchup.awayTeamId to matchup.awayScores.getBestBallGap()),
                        matchup.year,
                        matchup.week,
                    ),
                )
            }
            .sortedByDescending { entry -> entry.value }
            .take(itemsToInclude)

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

    private val standardScoreFunc: (TeamScores) -> Double = { scores -> scores.standardScore }
    private val bestBallScoreFunc: (TeamScores) -> Double = { scores -> scores.bestBallScore ?: scores.standardScore }

    private fun List<Matchup>.afterIncrease() = this.filter { it.year >= ESPNConfig.modernStartYear }
}
