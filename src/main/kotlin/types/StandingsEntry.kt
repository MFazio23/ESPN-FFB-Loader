package dev.mfazio.espnffb.types

data class StandingsIntEntry(
    val standardScoring: Int,
    val bestBallScoring: Int? = null,
    val topSixRankings: Int? = null,
)

data class StandingsDoubleEntry(
    val standardScoring: Double,
    val bestBallScoring: Double? = null,
    val topSixRankings: Double? = null,
)

fun Int.toStandingsIntEntry() = StandingsIntEntry(this)
fun Double.toStandingsDoubleEntry() = StandingsDoubleEntry(this)