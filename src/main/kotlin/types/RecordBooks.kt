package dev.mfazio.espnffb.types

data class RecordBooks(
    val standard: RecordBook,
    val modern: RecordBook,
    val bestBall: RecordBook,
    val currentYear: RecordBook,
    val currentYearBestBall: RecordBook,
)
