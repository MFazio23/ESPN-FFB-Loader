package dev.mfazio.espnffb.types.espn

data class ESPNTransactionCounter(
    val acquisitionBudgetSpent: Int,
    val acquisitions: Int,
    val drops: Int,
    val matchupAcquisitionTotals: Any,
    val misc: Int,
    val moveToActive: Int,
    val moveToIR: Int,
    val paid: Double,
    val teamCharges: Double,
    val trades: Int
)