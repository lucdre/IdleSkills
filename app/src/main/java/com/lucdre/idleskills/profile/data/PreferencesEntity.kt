package com.lucdre.idleskills.profile.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Database entity representing user preferences.
 * This is a singleton table with a fixed primary key ID of 0.
 *
 * @property id Unique local identifier (singleton key, defaults to 0).
 * @property isNotificationsEnabled True if notifications are enabled.
 * @property theme Selected theme preference (e.g. LIGHT, DARK, SYSTEM).
 */
@Entity(tableName = "user_preferences")
data class PreferencesEntity(
    @PrimaryKey val id: Int = 0,
    val isNotificationsEnabled: Boolean = true,
    val theme: String = "SYSTEM"
)
