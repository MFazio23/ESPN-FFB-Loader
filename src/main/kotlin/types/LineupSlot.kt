package dev.mfazio.espnffb.types

enum class LineupSlot(val espnId: Int, val displayOverride: String? = null) {
    QB(0),
    RB(2),
    WR(4),
    TE(6),
    DEF(16),
    K(17),
    RBWR(3, "RB/WR"),
    WRTE(5),
    BENCH(20, "Bench"),
    NA(-1, "N/A");

    val display = displayOverride ?: name

    companion object {
        fun fromESPNId(espnId: Int) = values().firstOrNull { it.espnId == espnId } ?: NA
    }
}