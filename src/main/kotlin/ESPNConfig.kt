package dev.mfazio.espnffb

import java.io.File

object ESPNConfig {
    const val baseFileDirectoryMac = "/Users/mfazio23/Development/Files/espn-ffb/"
    const val baseFileDirectoryAMac = "/Users/michael.fazio/Development/Files/espn-ffb/"
    const val baseFileDirectoryPC = "C:/dev/Files/espn-ffb/"

    val baseFileDirectory = when {
        File(baseFileDirectoryAMac).isDirectory -> baseFileDirectoryAMac
        File(baseFileDirectoryMac).isDirectory -> baseFileDirectoryMac
        else -> baseFileDirectoryPC
    }

    const val baseURL = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/ffl/"
    const val leagueID = "358793"
    const val historicalStartYear = 2009
    const val historicalEndYear = 2018
    const val modernStartYear = 2019
    const val modernEndYear = 2025
    const val currentYear = 2025
    const val startWeek = 1
    const val endWeek = 17

    fun championshipWeek(year: Int) = when {
        year < 2021 -> 16
        else -> 17
    }

    // Exclude any members who should not be included in the data calculations ever
    val excludedMemberIds = listOf(
        "047A1C53-72E7-4173-87EB-88EF5E4BAF7B" // Emily
    )
    // Exclude any members who should not be included in the data calculations, by year
    val excludedMemberIdsPerYear = mapOf(
        2009 to listOf("1CADD14C-2060-4856-BC7D-02C46D863D28"), // John
        2013 to listOf("57312B70-8D32-492F-8850-59850386652F"), // Breen
        2014 to listOf("57312B70-8D32-492F-8850-59850386652F"), // Breen
        2018 to listOf("C7CCE30B-298A-4C85-8265-37510D53E1CF"), // Alex
    )
    // Map of old member IDs to new member IDs, for when a member gets a new account
    // In hindsight, we should have created a new ID for everyone instead of using ESPN's.
    val mappedMemberIds = mapOf(
        "3C5922FA-CFDA-4E63-960E-22DB2A8AEC0B" to "F1509406-592B-43AF-A6E8-0F6BFB5638E7" // Ben
    )
}
