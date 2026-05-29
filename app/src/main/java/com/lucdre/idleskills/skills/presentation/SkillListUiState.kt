package com.lucdre.idleskills.skills.presentation

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.core.domain.OfflineProgressResult
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.training.TrainingMethod

/**
 * UI state for the skill list screen.
 *
 * Contains all the data needed to render the skill list screen and its components.
 *
 * @property skills List of all skills to display.
 * @property isLoading Whether data is currently being loaded.
 * @property error Error message to display if there was an error loading data.
 * @property activeSkill Name of the currently selected/active skill.
 * @property trainingMethods List of available training methods for the active skill.
 * @property activeTrainingMethod Currently selected training method.
 * @property activeCards Cards that are relevant to the active skill and training method.
 * @property playerProfile The current player profile data.
 * @property playerStatistics The current player statistics data.
 * @property offlineProgress The result of offline progress calculation to show in a popup.
 */
data class SkillListUiState(
    val skills: List<Skill> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeSkill: String? = null,
    val trainingMethods: List<TrainingMethod> = emptyList(),
    val activeTrainingMethod: TrainingMethod? = null,
    val activeCards: List<Card> = emptyList(),
    val playerProfile: PlayerProfile = PlayerProfile(),
    val playerStatistics: PlayerStatistics = PlayerStatistics(),
    val offlineProgress: OfflineProgressResult? = null,
    val expandedSkillName: String? = null
)
