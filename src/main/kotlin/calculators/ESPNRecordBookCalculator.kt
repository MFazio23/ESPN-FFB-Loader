package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.types.*
import dev.mfazio.utils.extensions.filterNotNullValues
import dev.mfazio.utils.extensions.printEach
import kotlin.math.abs

object ESPNRecordBookCalculator {

    private const val ITEMS_TO_INCLUDE = 10

    fun getRecordBookFromMatchups(matchups: List<Matchup>, skipCurrentYear: Boolean = true): RecordBook =
        RecordBook(
            mostPointsGame = getMostPointsInGame(matchups, includePlayoffs = true),
            mostPointsGameInPlayoffs = getMostPointsInGame(matchups, onlyPlayoffs = true),
            mostPointsSeason = getMostPointsInSeason(matchups),
            mostPointsSeasonWithPlayoffs = getMostPointsInSeason(matchups, includePlayoffs = true),
            mostPointsPerWeek = getMostPointsPerWeek(matchups, skipCurrentYear = skipCurrentYear),
            mostPointsPerWeekWithPlayoffs = getMostPointsPerWeek(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            mostPointsMatchup = getMostPointsInMatchup(matchups),
            fewestPointsGame = getFewestPointsInGame(matchups, includePlayoffs = true),
            fewestPointsGameInPlayoffs = getFewestPointsInGame(matchups, onlyPlayoffs = true),
            fewestPointsSeason = getFewestPointsInSeason(matchups, skipCurrentYear = skipCurrentYear),
            fewestPointsSeasonWithPlayoffs = getFewestPointsInSeason(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            fewestPointsPerWeek = getFewestPointsPerWeek(matchups, skipCurrentYear = skipCurrentYear),
            fewestPointsPerWeekWithPlayoffs = getFewestPointsPerWeek(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            fewestPointsMatchup = getFewestPointsInMatchup(matchups),
            smallestMarginOfVictory = getSmallestMarginOfVictory(matchups),
            largestMarginOfVictory = getLargestMarginOfVictory(matchups),
            mostPointsAllowed = getMostPointsAllowed(matchups),
            mostPointsAllowedWithPlayoffs = getMostPointsAllowed(matchups, includePlayoffs = true),
            mostPointsAllowedPerWeek = getMostPointsAllowedPerWeek(matchups, skipCurrentYear = skipCurrentYear),
            mostPointsAllowedPerWeekWithPlayoffs = getMostPointsAllowedPerWeek(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            fewestPointsAllowed = getFewestPointsAllowed(matchups, skipCurrentYear = skipCurrentYear),
            fewestPointsAllowedWithPlayoffs = getFewestPointsAllowed(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            fewestPointsAllowedPerWeek = getFewestPointsAllowedPerWeek(matchups, skipCurrentYear = skipCurrentYear),
            fewestPointsAllowedPerWeekWithPlayoffs = getFewestPointsAllowedPerWeek(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            longestWinningStreak = getLongestWinningStreak(matchups),
            longestWinningStreakWithPlayoffs = getLongestWinningStreak(matchups, includePlayoffs = true),
            longestLosingStreak = getLongestLosingStreak(matchups),
            longestLosingStreakWithPlayoffs = getLongestLosingStreak(matchups, includePlayoffs = true),
            lowestWinningScore = getLowestWinningScore(matchups),
            highestLosingScore = getHighestLosingScore(matchups),
            highestPointsPlus = getHighestPointsPlus(matchups),
            highestPointsPlusWithPlayoffs = getHighestPointsPlus(matchups, includePlayoffs = true),
            lowestPointsPlus = getLowestPointsPlus(matchups),
            lowestPointsPlusWithPlayoffs = getLowestPointsPlus(matchups, includePlayoffs = true),
            highestPointsAllowedPlus = getMostPointsAllowedPlus(matchups, skipCurrentYear = skipCurrentYear),
            highestPointsAllowedPlusWithPlayoffs = getMostPointsAllowedPlus(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
            lowestPointsAllowedPlus = getFewestPointsAllowedPlus(matchups, skipCurrentYear = skipCurrentYear),
            lowestPointsAllowedPlusWithPlayoffs = getFewestPointsAllowedPlus(matchups, includePlayoffs = true, skipCurrentYear = skipCurrentYear),
        )

    fun getModernRecordBook(allMatchups: List<Matchup>): RecordBook =
        getRecordBookFromMatchups(allMatchups.afterIncrease())

    fun getCurrentYearRecordBook(allMatchups: List<Matchup>): RecordBook =
        getRecordBookFromMatchups(allMatchups.filter { it.year == ESPNConfig.currentYear }, skipCurrentYear = false)

    fun getBestBallRecordBook(allMatchups: List<Matchup>): RecordBook =
        allMatchups.afterIncrease().let { matchups ->
            RecordBook(
                mostPointsGame = getMostPointsInGame(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                mostPointsGameInPlayoffs = getMostPointsInGame(matchups, onlyPlayoffs = true, scoreFunction = bestBallScoreFunc),
                mostPointsSeason = getMostPointsInSeason(matchups, scoreFunction = bestBallScoreFunc),
                mostPointsSeasonWithPlayoffs = getMostPointsInSeason(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                mostPointsPerWeek = getMostPointsPerWeek(matchups, scoreFunction = bestBallScoreFunc),
                mostPointsPerWeekWithPlayoffs = getMostPointsPerWeek(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                mostPointsMatchup = getMostPointsInMatchup(matchups, bestBallScoreFunc),
                fewestPointsGame = getFewestPointsInGame(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                fewestPointsGameInPlayoffs = getFewestPointsInGame(matchups, onlyPlayoffs = true, scoreFunction = bestBallScoreFunc),
                fewestPointsSeason = getFewestPointsInSeason(matchups, scoreFunction = bestBallScoreFunc),
                fewestPointsSeasonWithPlayoffs = getFewestPointsInSeason(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                fewestPointsPerWeek = getFewestPointsPerWeek(matchups, scoreFunction = bestBallScoreFunc),
                fewestPointsPerWeekWithPlayoffs = getFewestPointsPerWeek(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
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
                highestPointsPlus = getHighestPointsPlus(matchups, scoreFunction = bestBallScoreFunc),
                highestPointsPlusWithPlayoffs = getHighestPointsPlus(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                lowestPointsPlus = getLowestPointsPlus(matchups, scoreFunction = bestBallScoreFunc),
                lowestPointsPlusWithPlayoffs = getLowestPointsPlus(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                highestPointsAllowedPlus = getMostPointsAllowedPlus(matchups, scoreFunction = bestBallScoreFunc),
                highestPointsAllowedPlusWithPlayoffs = getMostPointsAllowedPlus(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                lowestPointsAllowedPlus = getFewestPointsAllowedPlus(matchups, scoreFunction = bestBallScoreFunc),
                lowestPointsAllowedPlusWithPlayoffs = getFewestPointsAllowedPlus(matchups, includePlayoffs = true, scoreFunction = bestBallScoreFunc),
                mostPointsMissed = getMostPointsInGame(matchups, includePlayoffs = true, scoreFunction = bestBallGapFunc),
                mostPointsMissedInSeason = getMostPointsInSeason(matchups, scoreFunction = bestBallGapFunc),
                mostPointsMissedInSeasonWithPlayoffs = getMostPointsInSeason(matchups, includePlayoffs = true, scoreFunction = bestBallGapFunc),
                highestPointsPlayedPctInSeason = getHighestPointsPlayedPctInSeason(matchups),
                highestPointsPlayedPctInSeasonWithPlayoffs = getHighestPointsPlayedPctInSeason(matchups, includePlayoffs = true),
                fewestPointsMissedInSeason = getFewestPointsInSeason(matchups, scoreFunction = bestBallGapFunc),
                fewestPointsMissedInSeasonWithPlayoffs = getFewestPointsInSeason(matchups, includePlayoffs = true, scoreFunction = bestBallGapFunc),
                lowestPointsPlayedPctInSeason = getLowestPointsPlayedPctInSeason(matchups),
                lowestPointsPlayedPctInSeasonWithPlayoffs = getLowestPointsPlayedPctInSeason(matchups, includePlayoffs = true),
            )
        }

    fun getMostPointsInGame(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        onlyPlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getProperMatchups(matchups, includePlayoffs = includePlayoffs, onlyPlayoffs = onlyPlayoffs)
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
        .take(ITEMS_TO_INCLUDE)

    private fun getFewestPointsInGame(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        onlyPlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getProperMatchups(matchups, includePlayoffs = includePlayoffs, onlyPlayoffs = onlyPlayoffs)
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
        .take(ITEMS_TO_INCLUDE)

    private fun getMostPointsInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsForInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
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
        .take(ITEMS_TO_INCLUDE)

    private fun getHighestPointsPlus(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsPlus(matchups, includePlayoffs, skipCurrentYear, scoreFunction).sortedByDescending { it.value }.take(ITEMS_TO_INCLUDE)

    private fun getLowestPointsPlus(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsPlus(matchups, includePlayoffs, skipCurrentYear, scoreFunction).sortedBy { it.value }.take(ITEMS_TO_INCLUDE)

    private fun getPointsPlus(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsForInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
        .flatMap { (year, teamScores) ->
            teamScores.map { (teamId, score) ->
                val averageScore = teamScores.filter { (id, _) -> teamId != id }.values.average()
                val plusScore = (score / averageScore) * 100
                RecordBookEntry(
                    plusScore,
                    mapOf(teamId to plusScore),
                    year,
                )
            }
        }

    private fun getFewestPointsInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsForInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
        .filter { (year, _) -> !skipCurrentYear || year != ESPNConfig.currentYear }
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
        .take(ITEMS_TO_INCLUDE)

    private fun getMostPointsPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc,
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsForInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
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
            .take(ITEMS_TO_INCLUDE)
    }

    private fun getFewestPointsPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsForInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
            .filter { (year, _) -> !skipCurrentYear || year != ESPNConfig.currentYear }
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
            .take(ITEMS_TO_INCLUDE)
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
        }.take(ITEMS_TO_INCLUDE)

    private fun getFewestPointsInMatchup(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) =
        matchups
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
            }.take(ITEMS_TO_INCLUDE)

    private fun getSmallestMarginOfVictory(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = matchups.sortedBy { abs(scoreFunction(it.homeScores) - scoreFunction(it.awayScores)) }.map { matchup ->
        val homeEntry = matchup.homeTeamId to scoreFunction(matchup.homeScores)
        val awayEntry = matchup.awayTeamId to scoreFunction(matchup.awayScores)
        RecordBookEntry(
            abs(scoreFunction(matchup.homeScores) - scoreFunction(matchup.awayScores)),
            mapOf(
                if(homeEntry.second > awayEntry.second) homeEntry else awayEntry,
                if(homeEntry.second > awayEntry.second) awayEntry else homeEntry,
            ),
            matchup.year,
            matchup.week,
        )
    }.take(ITEMS_TO_INCLUDE)

    private fun getLargestMarginOfVictory(
        matchups: List<Matchup>,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = matchups.sortedByDescending { abs(scoreFunction(it.homeScores) - scoreFunction(it.awayScores)) }.map { matchup ->
        val homeEntry = matchup.homeTeamId to scoreFunction(matchup.homeScores)
        val awayEntry = matchup.awayTeamId to scoreFunction(matchup.awayScores)
        RecordBookEntry(
            abs(scoreFunction(matchup.homeScores) - scoreFunction(matchup.awayScores)),
            mapOf(
                if(homeEntry.second > awayEntry.second) homeEntry else awayEntry,
                if(homeEntry.second > awayEntry.second) awayEntry else homeEntry,
            ),
            matchup.year,
            matchup.week,
        )
    }.take(ITEMS_TO_INCLUDE)

    private fun getMostPointsAllowed(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsAllowedInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
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
        .take(ITEMS_TO_INCLUDE)

    private fun getMostPointsAllowedPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsAllowedInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
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
            .take(ITEMS_TO_INCLUDE)
    }

    private fun getMostPointsAllowedPlus(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsAllowedInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
        .flatMap { (year, teamScores) ->
            teamScores.map { (teamId, score) ->
                val averageScore = teamScores.filter { (id, _) -> teamId != id }.values.average()
                val plusScore = (score / averageScore) * 100
                RecordBookEntry(
                    plusScore,
                    mapOf(teamId to plusScore),
                    year,
                )
            }
        }
        .sortedByDescending { it.value }
        .take(ITEMS_TO_INCLUDE)

    private fun getFewestPointsAllowed(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsAllowedInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
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
        .take(ITEMS_TO_INCLUDE)

    private fun getFewestPointsAllowedPerWeek(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ): List<RecordBookEntry> {
        val seasonLengths = getWeeksForSeasons(matchups, includePlayoffs)

        return getPointsAllowedInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
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
            .take(ITEMS_TO_INCLUDE)
    }

    private fun getFewestPointsAllowedPlus(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getPointsAllowedInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction)
        .flatMap { (year, teamScores) ->
            teamScores.map { (teamId, score) ->
                val averageScore = teamScores.filter { (id, _) -> teamId != id }.values.average()
                val plusScore = (score / averageScore) * 100
                RecordBookEntry(
                    plusScore,
                    mapOf(teamId to plusScore),
                    year,
                )
            }
        }
        .sortedBy { it.value }
        .take(ITEMS_TO_INCLUDE)

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
                            startWeek = if (streaks.startWeek == 0) streakItem.week else streaks.startWeek,
                            startYear = if (streaks.startWeek == 0) streakItem.year else streaks.startYear,
                            currentWeek = streakItem.week,
                            currentYear = streakItem.year,
                        )
                    } else {
                        val currentStreak = streaks.streaks + if (streaks.current == 0) {
                            emptyList()
                        } else {
                            listOf(
                                Streak(
                                    teamId = teamId,
                                    startWeek = streaks.startWeek,
                                    startYear = streaks.startYear,
                                    endWeek = streaks.currentWeek,
                                    endYear = streaks.currentYear,
                                    length = streaks.current
                                )
                            )
                        }
                        streaks.copy(
                            current = 0,
                            startWeek = 0,
                            startYear = 0,
                            currentWeek = 0,
                            currentYear = 0,
                            streaks = currentStreak
                        )
                    }
                }
                .let { streaks ->
                    if (streaks.current > 0) streaks.copy(
                        streaks = streaks.streaks + Streak(
                            teamId = teamId,
                            startWeek = streaks.startWeek,
                            startYear = streaks.startYear,
                            endWeek = streaks.currentWeek,
                            endYear = streaks.currentYear,
                            length = streaks.current
                        )
                    ) else streaks
                }
        }
        .values
        .flatMap { it.streaks }
        .sortedWith(compareBy({ -it.length }, { it.startYear }, { it.startWeek }))
        .take(ITEMS_TO_INCLUDE)
        .map { streak ->
            RecordBookEntry(
                value = streak.length.toDouble(),
                recordHolders = mapOf(streak.teamId to streak.length.toDouble()),
                season = streak.startYear,
                week = streak.startWeek,
                endSeason = streak.endYear,
                endWeek = streak.endWeek,
                intValue = true,
            )
        }

    private fun getLongestWinningStreak(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getLongestStreak(matchups, includePlayoffs, scoreFunction) { matchup, homeTeamWon ->
        listOf(
            matchup.homeTeamId to StreakItem(matchup.week, matchup.year, homeTeamWon),
            matchup.awayTeamId to StreakItem(matchup.week, matchup.year, !homeTeamWon),
        )
    }

    private fun getLongestLosingStreak(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        scoreFunction: (TeamScores) -> Double = standardScoreFunc
    ) = getLongestStreak(matchups, includePlayoffs, scoreFunction) { matchup, homeTeamWon ->
        listOf(
            matchup.homeTeamId to StreakItem(matchup.week, matchup.year, !homeTeamWon),
            matchup.awayTeamId to StreakItem(matchup.week, matchup.year, homeTeamWon),
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
        }.take(ITEMS_TO_INCLUDE)

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
        }.take(ITEMS_TO_INCLUDE)

    fun getPointsInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double,
        flatMapFunction: (Matchup) -> MappedTeamScores
    ) = getProperMatchups(matchups, includePlayoffs)
        .filter { matchup -> !skipCurrentYear || matchup.year != ESPNConfig.currentYear }
        .groupBy { it.year }
        .mapValues { (_, matchups) ->
            matchups
                .flatMap(flatMapFunction)
                .groupBy { (teamId, _) -> teamId }
                .mapValues { (_, teamScores) -> teamScores.sumOf { (_, scores) -> scoreFunction(scores) } }
        }

    fun getPointsForInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean,
        skipCurrentYear: Boolean = true,
        scoreFunction: (TeamScores) -> Double
    ) = getPointsInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction) { matchup ->
        listOf(matchup.homeTeamId to matchup.homeScores) + listOf(matchup.awayTeamId to matchup.awayScores)
    }

    private fun getPointsAllowedInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean,
        skipCurrentYear: Boolean,
        scoreFunction: (TeamScores) -> Double
    ) = getPointsInSeason(matchups, includePlayoffs, skipCurrentYear, scoreFunction) { matchup ->
        listOf(matchup.homeTeamId to matchup.awayScores) + listOf(matchup.awayTeamId to matchup.homeScores)
    }

    private fun getPointsPlayedPctInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        skipCurrentYear: Boolean = true,
    ): List<RecordBookEntry> {
        val modernMatchups = matchups.filter { it.year >= ESPNConfig.modernStartYear }
        val standardPoints = getPointsForInSeason(modernMatchups, includePlayoffs, skipCurrentYear, standardScoreFunc)
        val bestBallPoints = getPointsForInSeason(modernMatchups, includePlayoffs, skipCurrentYear, bestBallScoreFunc)

        val percentages = standardPoints.mapValues { (year, teamMap) ->
            val yearBestBall = bestBallPoints[year] ?: return@mapValues null

            teamMap.mapValues { (teamId, standardTotal) ->
                yearBestBall[teamId]?.let { bestBall ->
                    (standardTotal / bestBall) * 100.0
                } ?: -1.0
            }
        }.filterNotNullValues()

        return percentages.flatMap { (year, missedPercentages) ->
            missedPercentages.map { (teamId, score) ->
                RecordBookEntry(
                    score,
                    mapOf(teamId to score),
                    year,
                )
            }
        }
    }

    private fun getHighestPointsPlayedPctInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false
    ): List<RecordBookEntry> =
        getPointsPlayedPctInSeason(matchups, includePlayoffs).sortedByDescending { it.value }.take(ITEMS_TO_INCLUDE)

    private fun getLowestPointsPlayedPctInSeason(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false
    ): List<RecordBookEntry> =
        getPointsPlayedPctInSeason(matchups, includePlayoffs).sortedBy { it.value }.take(ITEMS_TO_INCLUDE)

    private fun getProperMatchups(
        matchups: List<Matchup>,
        includePlayoffs: Boolean = false,
        onlyPlayoffs: Boolean = false
    ): List<Matchup> = matchups
        .groupBy { it.year }
        .mapValues { (_, matchups) ->
            matchups
                .groupBy { it.week }
                .filterValues { weekMatchups ->
                    includePlayoffs ||
                    onlyPlayoffs && weekMatchups.any { it.playoffTierType != PlayoffTierType.None } ||
                    !onlyPlayoffs && weekMatchups.none { it.playoffTierType != PlayoffTierType.None }
                }
        }
        .values.flatMap { it.values }.flatten()

    private fun getWeeksForSeasons(matchups: List<Matchup>, includePlayoffs: Boolean = false) =
        getProperMatchups(matchups, includePlayoffs)
            .groupBy { it.year }
            .mapValues { (_, yearMatchups) -> yearMatchups.distinctBy { it.week }.size }

    private fun List<Matchup>.afterIncrease() = this.filter { it.year >= ESPNConfig.modernStartYear }
}
