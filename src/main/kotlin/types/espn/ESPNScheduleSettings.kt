package dev.mfazio.espnffb.types.espn

data class ESPNScheduleSettings(
    val divisions: List<ESPNDivision>,
    val matchupPeriods: Any,
    val periodTypeId: Int
)