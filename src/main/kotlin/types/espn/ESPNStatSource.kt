package dev.mfazio.espnffb.types.espn

enum class ESPNStatSource(val id: Int) {
    Actual(0),
    Projected(1);

    companion object {
        fun fromId(id: Int?) = ESPNStatSource.entries.firstOrNull { it.id == id }
    }
}