package com.lucdre.idleskills.profile.domain

import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType

/**
 * Accumulated player statistics.
 *
 * @property stats Map of skill -> method ID -> action count.
 */
@Immutable
data class PlayerStatistics(
    val stats: Map<String, Map<String, Int>> = emptyMap()
) {
    /**
     * Gets the total count of actions completed for a specific skill.
     *
     * @param skill The skill type.
     * @return Total actions for [skill].
     */
    fun getCountForSkill(skill: SkillType): Int {
        return stats[skill.name]?.values?.sum() ?: 0
    }

    /**
     * Gets the count of actions completed for a specific training method within a skill.
     *
     * @param skill The skill type.
     * @param methodType The training method type.
     * @return Actions for [methodType] in [skill].
     */
    fun getCountForMethod(skill: SkillType, methodType: TrainingMethodType): Int {
        val skillStats = stats[skill.name] ?: return 0
        return skillStats[methodType.id] ?: 0
    }
}
