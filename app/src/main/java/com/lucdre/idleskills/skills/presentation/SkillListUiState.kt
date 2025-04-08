package com.lucdre.idleskills.skills.presentation

import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.tools.Tool
import com.lucdre.idleskills.skills.domain.training.TrainingMethod

// UI State
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
