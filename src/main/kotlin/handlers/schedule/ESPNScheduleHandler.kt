package dev.mfazio.espnffb.handlers.schedule

import dev.mfazio.espnffb.converters.getMemberListFromScoreboards
import dev.mfazio.espnffb.converters.getTeamYearMapFromScoreboards
import dev.mfazio.espnffb.handlers.ESPNLocalFileHandler
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.schedule.MatchupType
import dev.mfazio.espnffb.types.schedule.ScheduleMatchup
import dev.mfazio.utils.extensions.crossProduct
import dev.mfazio.utils.extensions.printEach
import dev.mfazio.utils.random.flipCoin
import kotlin.system.measureTimeMillis

object ESPNScheduleHandler {
    val teamGroups = listOf(
        listOf(9, 14, 13, 1),   // Witz, Ben Green, Tim, Fazio
        listOf(15, 11, 17, 4),  // Nick, Rolando, Blayne, Ben Pelc
        listOf(12, 5, 3, 10)    // Tucker, Scott, James, Ben E.
    )

    val rivalryGames = listOf(
        1 to 11,    // Fazio vs. Rolando
        12 to 3,    // Tucker vs. James
        14 to 4,    // Ben Green vs. Ben Pelc
        5 to 9,     // Scott vs. Witz
        13 to 10,   // Tim vs. Ben
        17 to 15,   // Blayne vs. Nick
    )

    fun generateTeamsAndMembers(year: Int): Pair<List<Team>, List<Member>>? {
        val scoreboards = ESPNLocalFileHandler.loadAllLocalScoreboardFiles()

        val teamsMap = getTeamYearMapFromScoreboards(scoreboards)
        val members = getMemberListFromScoreboards(scoreboards)

        val thisYearsTeams = teamsMap[year] ?: return null

        return thisYearsTeams to members.filter { member -> thisYearsTeams.any { team -> team.owners.contains(member.id) } }
    }

    fun generateSchedule(year: Int) = measureTimeMillis {
        val (teams, members) = generateTeamsAndMembers(year) ?: return@measureTimeMillis

        var finalSchedule: List<ScheduleMatchup>
        var isValid: Boolean

        val weekOneMatchups = getRivalryMatchups(teams)

        val (firstGroupMatchups, secondGroupMatchups) = generateGroupMatchups(teams, weekOneMatchups)

        var generationAttempts = 0
        println("Generating schedule for $year...")
        do {
            generationAttempts++
            if (generationAttempts % 100_000 == 0) println("Generation attempt ${"%,d".format(generationAttempts)}")
            val otherMatchups =
                getRemainingMatchups(teams, weekOneMatchups + firstGroupMatchups + secondGroupMatchups)

            finalSchedule = weekOneMatchups + firstGroupMatchups + otherMatchups + secondGroupMatchups

            isValid = validateFinalSchedule(teams, finalSchedule)
        } while (!isValid)

        println("Generation attempts=${"%,d".format(generationAttempts)}")

        printMatchupCountsAndSchedule(teams, members, finalSchedule)
    }.let { time ->
        println("Schedule generated in ${time}ms")
    }

    fun getRivalryMatchups(teams: List<Team>): List<ScheduleMatchup> = rivalryGames.map { (a, b) ->
        val teamA = teams.first { it.id == a }
        val teamB = teams.first { it.id == b }

        val (first, second) = if (flipCoin()) {
            teamA to teamB
        } else {
            teamB to teamA
        }
        ScheduleMatchup(first, second, MatchupType.Rivalry)
    }

    fun generateGroupMatchups(
        teams: List<Team>,
        otherMatchups: List<ScheduleMatchup>
    ): Pair<List<ScheduleMatchup>, List<ScheduleMatchup>> {
        val teamGroupMatchupSplit = teamGroups.map { group ->
            val groupTeams = group.map { teamId -> teams.first { it.id == teamId } }

            groupTeams
                .crossProduct(groupTeams)
                .filter { (a, b) -> a != b }
                .map { (a, b) -> ScheduleMatchup(a, b, MatchupType.PlaceGroup) }
                .fold(emptyList<ScheduleMatchup>() to emptyList<ScheduleMatchup>()) { matchups, matchup ->
                    val matchupList = matchups.first + matchups.second
                    if (matchupList.any { it.contains(matchup.home) && it.contains(matchup.away) }) {
                        matchups
                    } else if (otherMatchups.any { it.contains(matchup.home) && it.contains(matchup.away) }) {
                        val newMatchup = otherMatchups
                            .first { it.contains(matchup.home) && it.contains(matchup.away) }
                            .let { if (it.home == matchup.home) matchup.flipped() else matchup }
                        matchups.first to matchups.second + newMatchup
                    } else {
                        val (matchupA, matchupB) = if (flipCoin()) {
                            matchup to matchup.flipped()
                        } else {
                            matchup.flipped() to matchup
                        }
                        matchups.first + matchupA to matchups.second + matchupB
                    }
                }
        }

        val firstGroupMatchupStart = teamGroupMatchupSplit.flatMap { it.first }
        val secondGroupMatchupStart = teamGroupMatchupSplit.flatMap { it.second }

        val extraTeams = teams
            .filter { team -> firstGroupMatchupStart.count { matchup -> matchup.contains(team) } < 3 }
            .groupBy { team -> teamGroups.indexOfFirst { group -> group.contains(team.id) } }
            .values
            .toList()

        val extraMatchups = extraTeams
            .chunked(2)
            .flatMap { (a, b) -> a.shuffled().zip(b.shuffled()) }
            .map { (a, b) -> ScheduleMatchup(a, b) }

        val firstGroupMatchups =
            splitMatchupsIntoWeeks(teams, firstGroupMatchupStart + extraMatchups).values.flatten()
        val secondGroupMatchups = splitMatchupsIntoWeeks(teams, secondGroupMatchupStart).values.flatten()

        return firstGroupMatchups to secondGroupMatchups
    }

    fun getRemainingMatchups(teams: List<Team>, currentMatchups: List<ScheduleMatchup>): List<ScheduleMatchup> {
        val remainingMatchups = teams
            .crossProduct(teams)
            .filter { (a, b) -> a != b }
            .map { (a, b) -> ScheduleMatchup(a, b) }
            .fold(emptyList<ScheduleMatchup>()) { matchups, matchup ->
                val matchupList = matchups + currentMatchups
                if (matchupList.any { (a, b) -> matchup.contains(a) && matchup.contains(b) }) {
                    matchups
                } else {
                    val newMatchup = if (flipCoin()) {
                        matchup
                    } else {
                        matchup.flipped()
                    }
                    matchups + newMatchup
                }
            }.shuffled()

        val remainingWeekCount = remainingMatchups.size / (teams.size / 2)

        val remainingMatchupsByWeek = (0 until remainingWeekCount).associateWith { mutableListOf<ScheduleMatchup>() }

        remainingMatchups.map { matchup ->
            (0 until remainingWeekCount).forEach { week ->
                if (remainingMatchupsByWeek[week]?.none { it.contains(matchup.home) || it.contains(matchup.away) } == true) {
                    remainingMatchupsByWeek[week]?.add(matchup)
                    return@map
                }
            }
        }

        return remainingMatchupsByWeek.values.flatten()
    }

    fun splitMatchupsIntoWeeks(teams: List<Team>, matchups: List<ScheduleMatchup>): Map<Int, List<ScheduleMatchup>> {
        val weeks = matchups.count { matchup -> matchup.contains(matchups.first().home) }

        val matchupWeeks = mutableMapOf<Int, MutableList<ScheduleMatchup>>().withDefault { mutableListOf() }

        var isValid: Boolean

        do {
            matchupWeeks.clear()
            matchups.shuffled().forEachIndexed { _, matchup ->
                (0 until weeks).forEach { week ->
                    if (matchupWeeks[week]?.any { it.contains(matchup.home) || it.contains(matchup.away) } != true) {
                        matchupWeeks[week]?.add(matchup) ?: run {
                            matchupWeeks[week] = mutableListOf(matchup)
                        }
                        return@forEachIndexed
                    }

                }
            }
            isValid = teams.all { team ->
                matchupWeeks.values.all { weekMatchups ->
                    weekMatchups.count { it.contains(team) } == 1
                }
            }
        } while (!isValid)

        return matchupWeeks
    }

    fun printMatchupCountsAndSchedule(teams: List<Team>, members: List<Member>, finalSchedule: List<ScheduleMatchup>) {
        println("====== MATCHUP COUNTS ======")
        teams.forEach { team ->
            val member = members.first { it.id == team.owners.first() }
            val teamMatchups = finalSchedule.count { it.contains(team) }
            val homeCount = finalSchedule.count { it.home == team }
            println("${member.fullName}: $teamMatchups matchups ($homeCount home, ${teamMatchups - homeCount} away)")
        }

        println()
        println("====== FINAL SCHEDULE ======")

        val scheduleWeeks = finalSchedule.chunked(6)

        scheduleWeeks.forEachIndexed { weekNum, weekMatchups ->
            println("*** Week ${weekNum + 1} ***")
            weekMatchups.forEach { (home, away) ->
                val homeMember = members.first { it.id == home.owners.first() }
                val awayMember = members.first { it.id == away.owners.first() }
                //if (home.id == 1 || away.id == 1) {
                println("${homeMember.fullName} vs. ${awayMember.fullName}")
                //}
            }
            println()
        }

        if (!validateFinalSchedule(teams, finalSchedule)) {
            println("!!Schedule is not valid!!")
        } else {
            println("Schedule is valid!")
        }
    }

    fun validateFinalSchedule(teams: List<Team>, schedule: List<ScheduleMatchup>) = ScheduleValidator.validateSchedule(
        teams,
        schedule,
        ValidationType.WeekOneRivalry,
        //ValidationType.HomeAway,
        ValidationType.OnePerWeek,
        ValidationType.TwoInGroup,
        ValidationType.OneOutOfGroup
    )
}

fun main() {
    ESPNScheduleHandler.generateSchedule(2024)
}