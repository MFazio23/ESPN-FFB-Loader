package dev.mfazio.espnffb.various.calcs

import dev.mfazio.espnffb.calculators.ESPNStandingsCalculator
import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.PlayoffTierType
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.various.VariousFactCard
import dev.mfazio.espnffb.various.VariousFactEntry
import dev.mfazio.espnffb.various.VariousFactGenerator
import dev.mfazio.utils.extensions.orZero
import dev.mfazio.utils.extensions.printEach
import dev.mfazio.utils.extensions.toTwoDigits

object LuckyWinnersUnluckyLosers : VariousFactGenerator {
    override fun generate(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: Map<Int, List<Team>>,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard> {
        val (luckyWinners, unluckyLosers) = getLuckyWinnersUnluckyLosers(matchups, members, allTeams, teamsMap)

        return listOf(
            VariousFactCard(
                title = luckyWinnersTitle,
                subtitle = luckyWinnersSubtitle,
                entries = luckyWinners.mapIndexed { index, winnerLoser ->
                    VariousFactEntry(
                        number = index + 1,
                        title = winnerLoser.toTitle(),
                        subtitle = winnerLoser.toSubTitle()
                    )
                }
            ),
            VariousFactCard(
                title = unluckyLosersTitle,
                subtitle = unluckyLosersSubtitle,
                entries = unluckyLosers.mapIndexed { index, winnerLoser ->
                    VariousFactEntry(
                        number = index + 1,
                        title = winnerLoser.toTitle(),
                        subtitle = winnerLoser.toSubTitle()
                    )
                }
            )
        )
    }

    fun getLuckyWinnersUnluckyLosers(
        matchups: List<Matchup>,
        members: List<Member>,
        allTeams: List<Team>,
        teamsMap: Map<Int, List<Team>>,
        includePlayoffs: Boolean = false,
    ): Pair<List<WinnerLoser>, List<WinnerLoser>> {
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

                        if (wins > losses && pointsScored < pointsAgainst) {
                            luckyWinners.add(winnerLoser)
                        }

                        if (wins < losses && pointsScored > pointsAgainst) {
                            unluckyLosers.add(winnerLoser)
                        }
                    }
                }
            }

        return Pair(
            luckyWinners.sortedBy { it.pointDifferential },
            unluckyLosers.sortedByDescending { it.pointDifferential }
        )
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
        val (luckyWinners, unluckyLosers) = getLuckyWinnersUnluckyLosers(
            matchups,
            members,
            allTeams,
            teamsMap,
            includePlayoffs
        )

        println("===== Lucky  winners =====")
        luckyWinners.printEach()

        println("===== Unlucky losers =====")
        unluckyLosers.printEach()
    }

    const val luckyWinnersTitle = "Lucky Winners"
    const val luckyWinnersSubtitle = "Teams with a winning record and a negative point differential"
    const val unluckyLosersTitle = "Unlucky Losers"
    const val unluckyLosersSubtitle = "Teams with a losing record and a positive point differential"
}

data class WinnerLoser(
    val name: String,
    val year: Int,
    val wins: Int,
    val losses: Int,
    val pointsScored: Double,
    val pointsAgainst: Double,
) {
    val pointDifferential = pointsScored - pointsAgainst
    override fun toString(): String =
        "$name ($year): $wins-$losses, ${pointsScored.toTwoDigits()}-${pointsAgainst.toTwoDigits()} (${pointDifferential.toTwoDigits()})"
    fun toTitle(): String = "$name ($year): $wins-$losses (${pointDifferential.toTwoDigits()} PD)"
    fun toSubTitle(): String = "${pointsScored.toTwoDigits()}-${pointsAgainst.toTwoDigits()}"
}
