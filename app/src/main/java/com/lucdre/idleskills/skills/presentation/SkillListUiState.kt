package com.lucdre.idleskills.skills.presentation

import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.training.TrainingMethod

/**
 * UI state for the skill list screen.
 *
 * Contains all the data needed to render the skill list screen and its components.
 *
 * @property skills List of all skills to display
 * @property isLoading Whether data is currently being loaded
 * @property error Error message to display if there was an error loading data
 * @property activeSkill Name of the currently selected/active skill
 * @property trainingMethods List of available training methods for the active skill
 * @property activeTrainingMethod Currently selected training method
 * @property tools List of available tools for the active skill
 * @property activeTool Currently selected tool
 * @property trainingProgress Progress of the current training action (0-1)
 */
data class SkillListUiState(
    val skills: List<Skill> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeSkill: String? = null,
    val trainingMethods: List<TrainingMethod> = emptyList(),
    val activeTrainingMethod: TrainingMethod? = null,
    val tools: List<Tool> = emptyList(),
    val activeTool: Tool? = null,
    val trainingProgress: Float = 0f // Progress from 0 to 1.0
)