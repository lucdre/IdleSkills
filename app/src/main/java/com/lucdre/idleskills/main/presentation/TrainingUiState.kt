package com.lucdre.idleskills.main.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.profile.domain.PlayerProfile
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
 * Training progress and session XP.
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
 *
 * @property skills List of all visible skills.
 * @property trainingMethods List of training methods for the currently expanded skill.
 * @property expandedSkillName The name of the skill that is currently expanded in the UI.
 * @property activeTrainingSkill The type of the skill currently being trained.
 * @property activeTrainingMethod The method currently being used for training.
 * @property activeCards The list of cards currently providing bonuses to the active training.
 * @property levelInfo XP and level progress information for the active skill.
 * @property isLoading Whether the skills state is currently being loaded.
 * @property error An optional error message to display in the UI.
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
 *
 * @property lootBoxes List of all owned loot boxes.
 * @property lastRewards The rewards obtained from the last opened loot box.
 * @property isSpriteVisible Whether the loot sprite is currently visible on screen.
 * @property spritePosition The screen position of the loot sprite.
 */
@Immutable
data class TrainingLootState(
    val lootBoxes: List<LootBox> = emptyList(),
    val lastRewards: Map<CardType, Int>? = null,
    val isSpriteVisible: Boolean = false,
    val spritePosition: Offset = Offset(0.5f, 0.5f),
    val isLoading: Boolean = false
)

/**
 * State related to the player session, profile, and inventory.
 *
 * @property playerProfile The current player profile data.
 * @property offlineProgress The result of the last offline progress calculation.
 * @property regionName The display name of the current region.
 * @property inventoryItems The list of items currently in the player's inventory.
 */
@Immutable
data class TrainingSessionState(
    val playerProfile: PlayerProfile = PlayerProfile(),
    val offlineProgress: OfflineProgressResult? = null,
    val regionName: String = "",
    val inventoryItems: List<Item> = emptyList(),
    val isLoading: Boolean = false
)
