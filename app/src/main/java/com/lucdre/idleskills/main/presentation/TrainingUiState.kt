package com.lucdre.idleskills.main.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.core.domain.OfflineProgressResult
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethod

/**
 * Information about a skill's level and XP progress.
 */
@Immutable
data class LevelInfo(
    val currentLevel: Int = 1,
    val totalXp: Int = 0,
    val nextLevelXp: Int = 0,
    val xpToNextLevel: Int = 0,
    val progressDecimal: Float = 0f
)

/**
 * Highly volatile training state that updates frequently (e.g., progress bar).
 */
@Immutable
data class ActiveTrainingState(
    val trainingProgress: Float = 0f,
    val sessionXpGained: Int = 0,
    val xpPerHour: Int = 0,
    val timeToLevelUpMs: Long = 0,
)

/**
 * State related to skills and their training methods.
 */
@Immutable
data class TrainingSkillsState(
    val skills: List<Skill> = emptyList(),
    val trainingMethods: List<TrainingMethod> = emptyList(),
    val expandedSkillName: String? = null,
    val activeTrainingSkill: SkillType? = null,
    val activeTrainingMethod: TrainingMethod? = null,
    val activeCards: List<Card> = emptyList(),
    val levelInfo: LevelInfo = LevelInfo(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * State related to loot boxes and rewards.
 */
@Immutable
data class TrainingLootState(
    val lootBoxes: List<LootBox> = emptyList(),
    val lastRewards: Map<CardType, Int>? = null,
    val isSpriteVisible: Boolean = false,
    val spritePosition: Offset = Offset(0.5f, 0.5f)
)

/**
 * State related to the player session, profile, and inventory.
 */
@Immutable
data class TrainingSessionState(
    val playerProfile: PlayerProfile = PlayerProfile(),
    val playerStatistics: PlayerStatistics = PlayerStatistics(),
    val offlineProgress: OfflineProgressResult? = null,
    val regionName: String = "",
    val inventoryItems: List<Item> = emptyList()
)
