package com.lucdre.idleskills.profile.domain

import androidx.compose.runtime.Immutable

/**
 * Basic player profile.
 *
 * @property username The player's display name.
 */
@Immutable
data class PlayerProfile(
    val username: String = ""
)
