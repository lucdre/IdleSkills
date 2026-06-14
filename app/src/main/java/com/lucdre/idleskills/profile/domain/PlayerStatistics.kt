package com.lucdre.idleskills.profile.domain

import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Represents the player's accumulated statistics for various skills and training methods.
 *
 * @property stats A nested map where the first key is the skill name, the second key is the
 *                training method name, and the value is the total number of actions completed.
 */
@Immutable
data class PlayerStatistics(
    val stats: Map<String, Map<String, Int>> = emptyMap()
) {
    /**
     * Gets the total count of actions completed for a specific skill.
     *
     * @param skill The skill type.
     * @return The total number of actions completed.
     */
    fun getCountForSkill(skill: SkillType): Int {
        return stats[skill.name]?.values?.sum() ?: 0
    }

    /**
     * Gets the count of actions completed for a specific training method within a skill.
     *
     * @param skill The skill type.
     * @param methodName The name of the training method.
     * @return The number of actions completed.
     */
    fun getCountForMethod(skill: SkillType, methodName: String): Int {
        val skillStats = stats[skill.name] ?: return 0
        return skillStats[methodName] ?: 0
    }
}
