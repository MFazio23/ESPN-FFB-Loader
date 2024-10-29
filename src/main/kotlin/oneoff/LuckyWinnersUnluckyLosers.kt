package dev.mfazio.espnffb.oneoff

import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.PlayoffTierType
import dev.mfazio.espnffb.types.Team
import dev.mfazio.utils.extensions.orZero
import dev.mfazio.utils.extensions.printEach
import dev.mfazio.utils.extensions.toTwoDigits


data class WinnerLoser(
    val name: String,
    val year: Int,
    val wins: Int,
    val losses: Int,
    val pointsScored: Double,
    val pointsAgainst: Double,
) {
    private val pointDifferential = pointsScored - pointsAgainst
    override fun toString(): String =
        "$name ($year): $wins-$losses, ${pointsScored.toTwoDigits()}-${pointsAgainst.toTwoDigits()} (${pointDifferential.toTwoDigits()})"
}

/**
 * This function prints out any teams that had either:
 *  - a winning record with a negative point differential
 *  - a losing record with a positive point differential
 */

fun printLuckyWinnersUnluckyLosers(
    matchups: List<Matchup>,
    members: List<Member>,
    allTeams: List<Team>,
    teamsMap: Map<Int, List<Team>>,
    includePlayoffs: Boolean = false,
) {
    val luckyWinners = mutableListOf<WinnerLoser>()
    val unluckyLosers = mutableListOf<WinnerLoser>()

    matchups
        .filter { includePlayoffs || (it.playoffTierType == null || it.playoffTierType == PlayoffTierType.None) }
        .groupBy { it.year }
        .forEach { (year, yearMatchups) ->
            val leagueStandings = ESPNStandingsCalculator.getStandingsFromMatchups(
                yearMatchups,
                members,
                allTeams,
                teamsMap,
            ).filter { it.pointsScored?.standardScoring.orZero() > 0.0 }

            leagueStandings.forEach { standings ->
                val wins = standings.wins?.standardScoring.orZero()
                val losses = standings.losses?.standardScoring.orZero()
                val pointsScored = standings.pointsScored?.standardScoring.orZero()
                val pointsAgainst = standings.pointsAgainst?.standardScoring.orZero()

                standings.member?.let { member ->
                    val winnerLoser = WinnerLoser(
                        member.fullName,
                        year,
                        wins,
                        losses,
                        pointsScored,
                        pointsAgainst
                    )

                    //println(winnerLoser)

                    if (wins > losses && pointsScored < pointsAgainst) {
                        luckyWinners.add(winnerLoser)
                    }

                    if (wins < losses && pointsScored > pointsAgainst) {
                        unluckyLosers.add(winnerLoser)
                    }
                }
            }
        }

    println("===== Lucky  winners =====")
    luckyWinners.printEach()

    println("===== Unlucky losers =====")
    unluckyLosers.printEach()
}
