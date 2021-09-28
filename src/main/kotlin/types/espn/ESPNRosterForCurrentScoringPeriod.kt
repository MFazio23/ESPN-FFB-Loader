package dev.mfazio.espnffb.types.espn

data class ESPNRosterForCurrentScoringPeriod(
    val appliedStatTotal: Double,
    val entries: List<ESPNEntry>
)