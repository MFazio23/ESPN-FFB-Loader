package dev.mfazio.espnffb.types

data class RecordBookEntry(
    val value: Double,
    val recordHolders: Map<Int, Double>,
    val season: Int? = null,
    val week: Int? = null,
    val intValue: Boolean = false,
) {
    companion object {
        fun getDefault() = RecordBookEntry(-1.0, mapOf(), -1)
    }
}
