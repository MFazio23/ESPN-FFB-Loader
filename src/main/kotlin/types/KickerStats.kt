package dev.mfazio.espnffb.types

import dev.mfazio.utils.extensions.getOrIntZero


data class KickerStats(
    val xp: Int,
    val xpMissed: Int,
    val underForty: Int,
    val underFortyMissed: Int,
    val forty: Int,
    val fiftyPlus: Int,
) {

    fun getModernScore() = ((xp + underForty) * 3) - (xpMissed + underFortyMissed) + (forty * 4) + (fiftyPlus * 5)

    companion object {
        fun fromESPNStats(espnStats: Map<String, Double>) =
            KickerStats(
                xp = espnStats.getOrIntZero("86"),
                xpMissed = espnStats.getOrIntZero("88"),
                underForty = espnStats.getOrIntZero("80"),
                underFortyMissed = espnStats.getOrIntZero("81") - espnStats.getOrIntZero("80"),
                forty = espnStats.getOrIntZero("77"),
                fiftyPlus = espnStats.getOrIntZero("74"),
            )
    }
}