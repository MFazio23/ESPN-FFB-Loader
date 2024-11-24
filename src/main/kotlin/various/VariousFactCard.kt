package dev.mfazio.espnffb.various


data class VariousFactCard(
    val title: String,
    val subtitle: String? = null,
    val entries: List<VariousFactEntry>,
)
