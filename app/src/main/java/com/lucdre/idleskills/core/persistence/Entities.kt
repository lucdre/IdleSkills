package com.lucdre.idleskills.core.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucdre.idleskills.region.domain.Region

/**
 * Entity representing the player's profile and current state.
 * One row, one character. (ID = 0)
 */
@Entity(tableName = "player_profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 0,
    val username: String = "",
    val currentRegion: Region = Region.FIRST_REGION,
    val availablePrestigePoints: Int = 0,
    val totalPrestigePoints: Int = 0,
    val unlockedSkillTreeNodes: Set<String> = emptySet(),
    val activeSkillName: String? = null,
    val activeMethodName: String? = null,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
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
