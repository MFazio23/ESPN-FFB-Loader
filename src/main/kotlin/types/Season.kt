package dev.mfazio.espnffb.types

interface Season {
    val id: String
    val eraId: String
    val title: String
    val subtitle: String?
    val year: Int
    val wins: Int
    val losses: Int
    val isChampion: Boolean
}
