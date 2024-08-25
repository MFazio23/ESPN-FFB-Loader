package dev.mfazio.espnffb.types

data class TeamMemberSummary(
    val id: String,
    val name: String,
    val wins: Int,
    val losses: Int,
    val championships: Int = 0,
    val eras: List<Era> = emptyList(),
)
