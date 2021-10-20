package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.types.espn.ESPNMember

data class Member(
    val id: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
) {
    val fullName = "$firstName $lastName"

    companion object {
        fun fromESPNMember(espnMember: ESPNMember) =
            Member(
                id = espnMember.id,
                userName = espnMember.displayName,
                firstName = espnMember.firstName.trim(),
                lastName = espnMember.lastName.trim(),
            )
    }
}
