package com.lucdre.idleskills.profile.domain

import androidx.compose.runtime.Immutable

/**
 * Basic player profile.
 *
 * @property playerId Unique global identifier for the player.
 * @property username The player's display name.
 */
@Immutable
data class PlayerProfile(
    val playerId: String = "",
    val username: String = "",
    val hasCompletedSetup: Boolean = false
)
