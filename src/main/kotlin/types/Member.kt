package dev.mfazio.espnffb.types

import dev.mfazio.espnffb.types.espn.ESPNMember

data class Member(
    val id: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
) {
    companion object {
        fun fromESPNMember(espnMember: ESPNMember) =
            Member(
                id = espnMember.id,
                userName = espnMember.displayName,
                firstName = espnMember.firstName,
                lastName = espnMember.lastName,
            )
    }
}
