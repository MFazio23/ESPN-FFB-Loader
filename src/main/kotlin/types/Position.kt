package dev.mfazio.espnffb.types

enum class Position(val id: Int) {
    QB(1),
    RB(2),
    WR(3),
    TE(4),
    K(5),
    DEF(16),
    NA(-1);

    companion object {
        fun fromESPNId(id: Int) = values().firstOrNull { it.id == id } ?: NA
    }
}
