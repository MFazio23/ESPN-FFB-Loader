package dev.mfazio.espnffb.various

import dev.mfazio.espnffb.types.Matchup
import dev.mfazio.espnffb.types.Member
import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.espn.ESPNScoreboard

interface VariousFactGenerator {
    fun generate(
        scoreboards: List<ESPNScoreboard>,
        matchups: List<Matchup>,
        teamsMap: Map<Int, List<Team>>,
        allTeams: List<Team>,
        members: List<Member>
    ): List<VariousFactCard>
}
