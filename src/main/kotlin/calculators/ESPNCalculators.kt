package dev.mfazio.espnffb.calculators

import dev.mfazio.espnffb.types.Player
import dev.mfazio.espnffb.types.Position

fun getBestBallLineup(players: List<Player>): List<Player> {
    val qb = players.filter { player -> player.position == Position.QB }.maxByOrNull { player -> player.points }
    val k = players.filter { player -> player.position == Position.K }.maxByOrNull { player -> player.points }
    val def = players.filter { player -> player.position == Position.DEF }.maxByOrNull { player -> player.points }
    val te = players.filter { player -> player.position == Position.TE }.maxByOrNull { player -> player.points }
    val (rb1, rb2) = players
        .filter { player -> player.position == Position.RB }
        .sortedByDescending { it.points }
        .take(2)
    val (wr1, wr2) = players
        .filter { player -> player.position == Position.WR }
        .sortedByDescending { it.points }
        .take(2)
    val (flex1, flex2) = getFlexPositions(
        players.filter {
            !listOf(qb, k, def, te, rb1, rb2, wr1, wr2).contains(it)
        }
    )

    return listOfNotNull(
        qb,
        rb1,
        rb2,
        flex1,
        wr1,
        wr2,
        flex2,
        te,
        def,
        k,
    )
}

fun getFlexPositions(players: List<Player>): List<Player> {
    val filteredPlayers = players.filter {
        listOf(Position.RB, Position.WR, Position.TE).contains(it.position)
    }

    val topPlayer = filteredPlayers.maxByOrNull { it.points } ?: return emptyList()

    return listOf(topPlayer) + listOfNotNull(when (topPlayer.position) {
        Position.RB -> filteredPlayers.filter { it.position != Position.RB }.maxByOrNull { it.points }
        Position.TE -> filteredPlayers.filter { it.position != Position.TE }.maxByOrNull { it.points }
        else -> filteredPlayers.filter { it.id != topPlayer.id }.maxByOrNull { it.points }
    })
}