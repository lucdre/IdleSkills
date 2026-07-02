package com.lucdre.idleskills.core.persistence

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.lucdre.idleskills.region.domain.Region

/**
 * Entity representing the player's core identity.
 * One row, one character. (ID = 0)
 *
 * @property id The local database row ID (singleton at 0).
 * @property playerId Unique global identifier for the player.
 * @property username The player's display name.
 */
@Entity(tableName = "player_profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 0,
    val playerId: String = "",
    val username: String = ""
)

/**
 * Entity representing user settings and preferences.
 * One row. (ID = 0)
 */
@Entity(tableName = "user_preferences")
data class PreferencesEntity(
    @PrimaryKey val id: Int = 0,
    val isNotificationsEnabled: Boolean = true,
    val theme: String = "SYSTEM"
)

/**
 * Entity representing the current session and state.
 * One row. (ID = 0)
 */
@Entity(tableName = "player_session")
data class SessionEntity(
    @PrimaryKey val id: Int = 0,
    val currentRegion: Region = Region.FIRST_REGION,
    val activeSkillName: String? = null,
    val activeMethodName: String? = null,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Entity representing the player's prestige and progression.
 * One row. (ID = 0)
 */
@Entity(tableName = "prestige_state")
data class PrestigeEntity(
    @PrimaryKey val id: Int = 0,
    val availablePrestigePoints: Int = 0,
    val totalPrestigePoints: Int = 0,
    val unlockedSkillTreeNodes: Set<String> = emptySet()
)

/**
 * Entity representing a skill's progress.
 */
@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val name: String,
    val xp: Int = 0
)

/**
 * Entity representing an owned card in the inventory.
 */
@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val cardType: String, // Using card type as unique identifier for now
    val name: String,
    val quantity: Int,
    val level: Int,
    val efficiencyBonus: Float,
    val iconResId: Int
)

/**
 * Entity representing unopened loot boxes.
 */
@Entity(tableName = "loot_boxes")
data class LootBoxEntity(
    @PrimaryKey val skillName: String,
    val count: Int
)

/**
 * Entity representing stackable items in the inventory.
 */
@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey val itemId: Int,
    val quantity: Int
)
