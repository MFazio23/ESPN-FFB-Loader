package dev.mfazio.espnffb.types.charts

interface ChartData {
    val chartId: String
    val type: ChartType
    val dataset: List<*>
    val seriesData: List<SeriesDataEntry>
}