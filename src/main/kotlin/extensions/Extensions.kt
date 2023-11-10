package dev.mfazio.espnffb.extensions

import dev.mfazio.espnffb.types.Team
import dev.mfazio.espnffb.types.TeamEra
import dev.mfazio.espnffb.types.TeamSeason
import dev.mfazio.espnffb.types.TeamSummary
import dev.mfazio.utils.extensions.printEach

fun List<TeamSeason>.getWinLossRecord(): Pair<Int, Int> =
    this.fold(0 to 0) { (wins, losses), teamSeason ->
        wins + teamSeason.wins to losses + teamSeason.losses
    }

fun Map<Team, List<TeamSeason>>.toTeamSummaries(): List<TeamSummary> =
    this.entries.map { (team, seasons) ->
        val (wins, losses) = seasons.getWinLossRecord()
        TeamSummary(
            teamId = team.id,
            teamName = team.fullName,
            wins = wins,
            losses = losses,
            championships = seasons.count { it.isChampion },
            eras = seasons.toTeamEras()
        )
    }

fun List<TeamSeason>.toTeamEras(): List<TeamEra> {
    val eras = mutableListOf<TeamEra>()

    val (firstSeason, otherSeasons) = this.sortedBy { it.year }.let {
        it.first() to it.drop(1)
    }

    var currentEra = TeamEra(
        teamName = firstSeason.team.fullName,
        startYear = firstSeason.year,
        endYear = firstSeason.year,
        owners = firstSeason.owners.map { it.fullName }
    )

    otherSeasons.forEach { season ->
        currentEra =
            if (currentEra.teamName != season.team.fullName || currentEra.owners != season.owners.map { it.fullName }) {
                eras.add(currentEra)
                TeamEra(
                    teamName = season.team.fullName,
                    startYear = season.year,
                    endYear = season.year,
                    owners = season.owners.map { it.fullName }
                )
            } else {
                currentEra.copy(
                    endYear = season.year,
                )
            }
    }

    eras.add(currentEra)

    return eras.toList()
}