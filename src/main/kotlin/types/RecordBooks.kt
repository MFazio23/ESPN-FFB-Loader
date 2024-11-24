package dev.mfazio.espnffb.types

data class RecordBooks(
    val latestYear: Int,
    val latestWeek: Int,
    val standard: RecordBook,
    val modern: RecordBook,
    val bestBall: RecordBook,
    val currentYear: RecordBook,
    val currentYearBestBall: RecordBook,
)
