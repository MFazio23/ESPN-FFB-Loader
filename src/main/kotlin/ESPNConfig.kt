package dev.mfazio.espnffb

import java.io.File

object ESPNConfig {
    const val baseFileDirectoryMac = "/Users/mfazio23/Development/Files/espn-ffb/"
    const val baseFileDirectoryPC = "C:/dev/Files/espn-ffb/"

    val baseFileDirectory = if (File(baseFileDirectoryMac).isDirectory) baseFileDirectoryMac else baseFileDirectoryPC

    const val baseURL = "https://fantasy.espn.com/apis/v3/games/ffl/"
    const val leagueID = "358793"
    const val historicalStartYear = 2009
    const val historicalEndYear = 2018
    const val modernStartYear = 2019
    const val modernEndYear = 2021
    const val currentYear = 2021
    const val startWeek = 1
    const val endWeek = 16
}