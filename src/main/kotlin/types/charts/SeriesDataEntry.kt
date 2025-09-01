package dev.mfazio.espnffb.types.charts

data class SeriesDataEntry(
    val dataKey: String,
    val label: String?,
    val stack: String? = null,
    val area: Boolean = false,
    val showMark: Boolean = true,
)
