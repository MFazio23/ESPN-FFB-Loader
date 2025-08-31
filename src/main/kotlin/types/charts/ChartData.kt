package dev.mfazio.espnffb.types.charts

interface ChartData {
    val chartId: String
    val type: ChartType
    val dataset: List<Any>
    val seriesData: List<SeriesDataEntry>
}