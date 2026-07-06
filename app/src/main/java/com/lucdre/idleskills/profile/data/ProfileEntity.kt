package com.lucdre.idleskills.profile.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Database entity representing the player's profile data.
 * This is a singleton table with a fixed primary key ID of 0.
 *
 * @property id Unique local identifier (singleton key, defaults to 0).
 * @property playerId Global unique identifier for the player.
 * @property username The chosen display name of the player.
 * @property hasCompletedSetup Flag indicating if the initial onboarding has been completed.
 */
@Entity(tableName = "player_profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 0,
    val playerId: String = "",
    val username: String = "",
    val hasCompletedSetup: Boolean = false
)
