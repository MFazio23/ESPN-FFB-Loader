package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.extensions.getOrZero

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
                xp = espnStats.getOrZero("86"),
                xpMissed = espnStats.getOrZero("88"),
                underForty = espnStats.getOrZero("80"),
                underFortyMissed = espnStats.getOrZero("81") - espnStats.getOrZero("80"),
                forty = espnStats.getOrZero("77"),
                fiftyPlus = espnStats.getOrZero("74"),
            )
    }
}