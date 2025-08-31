package dev.mfazio.espnffb.types.espn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESPNScheduleSettings(
    val divisions: List<ESPNDivision>,
    val matchupPeriods: Any,
    val periodTypeId: Int
)