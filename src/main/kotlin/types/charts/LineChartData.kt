package dev.mfazio.espnffb.types.charts

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LineChartData(
    @Transient override val type: ChartType = ChartType.Line,
    override val chartId: String,
    override val dataset: List<*>,
    override val seriesData: List<SeriesDataEntry>,
    val xAxis: LineChartAxis,
    val yAxis: LineChartAxis,
): ChartData

data class LineChartAxis(
    val dataKey: String? = null,
    val min: Double? = null,
    val max: Double? = null,
    val reverse: Boolean = false,
    val tickMinStep: Double? = null,
)