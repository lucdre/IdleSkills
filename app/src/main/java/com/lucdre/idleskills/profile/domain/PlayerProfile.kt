package com.lucdre.idleskills.profile.domain

import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.region.domain.Region

/**
 * Represents the player's basic profile.
 *
 * @property username The player's display name.
 * @property currentRegion The name of the current region the player is in.
 */
@Immutable
data class PlayerProfile(
    val username: String = "",
    val currentRegion: Region = Region.FIRST_REGION
)
