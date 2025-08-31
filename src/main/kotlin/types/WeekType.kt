package dev.mfazio.espnffb.types

import dev.mfazio.utils.extensions.orZero

enum class WeekType {
    Regular,
    FirstRound,
    SecondRound,
    Championship,
    Unknown;
    companion object {
        fun getWeekType(matchups: List<Matchup>, week: Int): WeekType {
            val weekTypes = matchups.groupBy { it.week }.mapValues { (_, matchups) ->
                val weekMatchupTypes = matchups.groupBy { it.playoffTierType }
                when (weekMatchupTypes[PlayoffTierType.WinnersBracket]?.size.orZero()) {
                    0 -> Regular
                    4 -> FirstRound
                    2 -> SecondRound
                    1 -> Championship
                    else -> Unknown
                }
            }

            return weekTypes[week] ?: Unknown
        }
    }
}
