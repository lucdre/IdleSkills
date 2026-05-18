package com.lucdre.idleskills.profile.domain

import com.lucdre.idleskills.region.domain.Region

/**
 * Represents the player's basic profile.
 *
 * @property username The player's display name.
 * @property favoriteSkill The skill chosen as the player's favorite at game start.
 * @property currentRegion The name of the current region the player is in.
 */
data class PlayerProfile(
    val username: String = "",
    val favoriteSkill: String = "",
    val currentRegion: Region = Region.FIRST_REGION
)
