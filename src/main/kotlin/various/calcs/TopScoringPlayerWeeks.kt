package dev.mfazio.espnffb.various.calcs

import dev.mfazio.espnffb.types.*
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.various.VariousFactCard
import dev.mfazio.espnffb.various.VariousFactEntry
import dev.mfazio.espnffb.various.VariousFactGenerator
import dev.mfazio.utils.extensions.printEach
import dev.mfazio.utils.extensions.toTwoDigits

object TopScoringPlayerWeeks : VariousFactGenerator {

    private const val title = "Top Scoring Player Weeks"
    private const val subtitle = "Modern seasons (2019-present) only"
    private const val nonQBTitle = "Top Scoring Non-QB Weeks"
    private const val nonQBSubtitle = "Modern seasons (2019-present) only"

    override fun generate(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard> {
        val topScoringPlayerWeeks = getTopScoringPlayerWeeks(matchups, teamsMap)
        val allPositionCard = VariousFactCard(
            title = title,
            subtitle = subtitle,
            entries = topScoringPlayerWeeks.take(10).mapIndexed { index, playerWeek ->
                VariousFactEntry(
                    number = index + 1,
                    title = playerWeek.toShortString(),
                    subtitle = playerWeek.toSubTitleString()
                )
            }
        )
        val topScoringNonQBWeeks = getTopScoringPlayerWeeks(matchups, teamsMap, listOf(Position.QB))
        val nonQBCard = VariousFactCard(
            title = nonQBTitle,
            subtitle = nonQBSubtitle,
            entries  = topScoringNonQBWeeks.take(10).mapIndexed { index, playerWeek ->
                VariousFactEntry(
                    number = index + 1,
                    title = playerWeek.toShortString(),
                    subtitle = playerWeek.toSubTitleString()
                )
            }
        )

        return listOf(allPositionCard, nonQBCard)
    }

    fun listTopScoringPlayerWeeks(
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
    ) {
        val topScoringPlayerWeeks = getTopScoringPlayerWeeks(matchups, teamsMap)

        // Get the top 25 player weeks in modern times
        topScoringPlayerWeeks.sortedByDescending { it.points }.take(25).printEach()
        println()
        // Get the number of top 100 weeks for each team in modern times
        topScoringPlayerWeeks
            .sortedByDescending { it.points }.take(100)
            .groupingBy { it.team.id }.eachCount().toList().sortedByDescending { (_, count) -> count }
            .mapIndexed { index, (teamId, count) ->
                println("${index + 1}: ${teamsMap[2024]?.firstOrNull { it.id == teamId }?.fullName} - $count")
            }
    }

    fun getTopScoringPlayerWeeks(
        matchups: List<Matchup>,
        teamsMap: TeamYearMap,
        excludedPositions: List<Position> = emptyList(),
    ): List<PlayerWeek> = matchups.flatMap { matchup ->
        val yearTeams = teamsMap[matchup.year]
        val homeTeam = yearTeams?.firstOrNull { it.id == matchup.homeTeamId }
        val awayTeam = yearTeams?.firstOrNull { it.id == matchup.awayTeamId }

        if (homeTeam != null && awayTeam != null) {
            (matchup.homePlayers?.filter { !excludedPositions.contains(it.position) }?.map { player ->
                PlayerWeek(
                    player,
                    player.points,
                    week = matchup.week,
                    year = matchup.year,
                    team = homeTeam,
                )
            } ?: emptyList()) +
                (matchup.awayPlayers?.filter { !excludedPositions.contains(it.position) }?.map { player ->
                    PlayerWeek(
                        player,
                        player.points,
                        week = matchup.week,
                        year = matchup.year,
                        team = awayTeam,
                    )
                } ?: emptyList())
        } else {
            emptyList()
        }
    }.sortedByDescending { it.points }
}

data class PlayerWeek(
    val player: Player,
    val points: Double,

    val week: Int,
    val year: Int,
    val team: Team,
) {
    fun toShortString(): String =
        "${player.name} (${player.position.name}) - ${points.toTwoDigits()} points"

    fun toSubTitleString(): String =
        "${team.fullName} - $year week $week"

    override fun toString(): String =
        "${this.toShortString()} (${this.toSubTitleString()})"
}
