package dev.mfazio.espnffb.various

import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import dev.mfazio.espnffb.various.calcs.LuckyWinnersUnluckyLosers
import dev.mfazio.espnffb.various.calcs.TopScoringPlayerWeeks

object VariousFactHandler {
    fun generateList(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: Map<Int, List<Team>>,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard> = listOf(
        TopScoringPlayerWeeks,
        LuckyWinnersUnluckyLosers,
    ).flatMap { it.generate(scoreboards, matchups, teamsMap, allTeams, members) }
}
