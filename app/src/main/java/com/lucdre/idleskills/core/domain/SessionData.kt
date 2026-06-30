package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType

/**
 * Snapshot of the player's current session state.
 */
data class SessionData(
    val activeSkill: SkillType?,
    val activeMethod: TrainingMethodType?,
    val currentRegion: Region,
    val lastSavedTimestamp: Long
)
