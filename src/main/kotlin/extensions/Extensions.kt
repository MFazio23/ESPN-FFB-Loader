package dev.mfazio.espnffb.extensions

import dev.mfazio.espnffb.types.*

fun List<Season>.getWinLossRecord(): Pair<Int, Int> =
    this.fold(0 to 0) { (wins, losses), season ->
        wins + season.wins to losses + season.losses
    }

fun List<Season>.toTeamMemberSummaries(id: String, name: String): TeamMemberSummary {
    val (wins, losses) = this.getWinLossRecord()
    return TeamMemberSummary(
        id = id,
        name = name,
        wins = wins,
        losses = losses,
        championships = this.count { it.isChampion },
        eras = this.toEras(),
    )
}

fun List<Season>.toEras(): List<Era> {
    val eras = mutableListOf<Era>()

    val (firstSeason, otherSeasons) = this.sortedBy { it.year }.let {
        it.first() to it.drop(1)
    }

    var currentEra = Era(
        id = firstSeason.eraId,
        title = firstSeason.title,
        subtitle = firstSeason.subtitle,
        startYear = firstSeason.year,
        endYear = firstSeason.year
    )

    otherSeasons.forEach { season ->
        currentEra =
            if (season.eraId != currentEra.id) {
                eras.add(currentEra)
                Era(
                    id = season.eraId,
                    title = season.title,
                    subtitle = season.subtitle,
                    startYear = season.year,
                    endYear = season.year
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
