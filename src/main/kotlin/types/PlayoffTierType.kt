package dev.mfazio.espnffb.types

enum class PlayoffTierType(val espnString: String) {
    WinnersBracket("WINNERS_BRACKET"),
    WinnersConsolation("WINNERS_CONSOLATION_LADDER"),
    LosersConsolation("LOSERS_CONSOLATION_LADDER"),
    None("NONE");

    companion object {
        fun fromESPNString(espnString: String) = values().firstOrNull { it.espnString == espnString }
    }
}