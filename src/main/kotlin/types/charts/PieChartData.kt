package dev.mfazio.espnffb.types.charts

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PieChartData(
    @Transient override val type: ChartType = ChartType.Pie,
    override val chartId: String,
    override val dataset: List<*>,
    override val seriesData: List<SeriesDataEntry>,
    val width: Double? = null,
    val height: Double? = null,
) : ChartData
