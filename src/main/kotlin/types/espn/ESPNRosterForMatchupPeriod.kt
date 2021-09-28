package dev.mfazio.espnffb.types.espn

data class ESPNRosterForMatchupPeriod(
    val appliedStatTotal: Double,
    val entries: List<ESPNEntry>
)