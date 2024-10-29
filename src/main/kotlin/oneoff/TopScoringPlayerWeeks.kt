package dev.mfazio.espnffb.oneoff

import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Player
import dev.mfazio.espnffb.types.Team
import dev.mfazio.utils.extensions.printEach
import dev.mfazio.utils.extensions.toTwoDigits

fun listTopScoringPlayerWeeks(
    matchups: List<Matchup>,
    teamsMap: Map<Int, List<Team>>
) {

    val playerWeeks = matchups.flatMap { matchup ->
        val yearTeams = teamsMap[matchup.year]
        val homeTeam = yearTeams?.firstOrNull { it.id == matchup.homeTeamId }
        val awayTeam = yearTeams?.firstOrNull { it.id == matchup.awayTeamId }

        if (homeTeam != null && awayTeam != null) {
            (matchup.homePlayers?.map { player ->
                PlayerWeek(
                    player,
                    player.points,
                    week = matchup.week,
                    year = matchup.year,
                    team = homeTeam,
                )
            } ?: emptyList()) +
                (matchup.awayPlayers?.map { player ->
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
    }

    // Get the top 25 player weeks in modern times
    playerWeeks.sortedByDescending { it.points }.take(25).printEach()
    println()
    // Get the number of top 100 weeks for each team in modern times
    playerWeeks
        .sortedByDescending { it.points }.take(100)
        .groupingBy { it.team.id }.eachCount().toList().sortedByDescending { (_, count) -> count }
        .map { (teamId, count) ->
            println("${teamsMap[2024]?.firstOrNull { it.id == teamId }?.fullName} - $count")
        }
}


data class PlayerWeek(
    val player: Player,
    val points: Double,

    val week: Int,
    val year: Int,
    val team: Team,
) {
    override fun toString(): String =
        "${player.name} (${player.position.name}) - ${points.toTwoDigits()} (${team.fullName} - $year week $week)"
}
