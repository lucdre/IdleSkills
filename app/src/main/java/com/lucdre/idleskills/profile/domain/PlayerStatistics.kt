package com.lucdre.idleskills.profile.domain

/**
 * Represents the player's accumulated statistics for various skills and training methods.
 *
 * @property stats A nested map where the first key is the skill name, the second key is the
 *                training method name, and the value is the total number of actions completed.
 */
data class PlayerStatistics(
    val stats: Map<String, Map<String, Int>> = emptyMap()
) {
    /**
     * Gets the total count of actions completed for a specific skill.
     *
     * @param skillName The name of the skill to get the total count for.
     * @return The total number of actions completed across all training methods for that skill.
     */
    fun getCountForSkill(skillName: String): Int {
        return stats[skillName]?.values?.sum() ?: 0
    }

    /**
     * Gets the count of actions completed for a specific training method within a skill.
     *
     * @param skillName The name of the skill.
     * @param methodName The name of the training method.
     * @return The number of actions completed for that specific method.
     */
    fun getCountForMethod(skillName: String, methodName: String): Int {
        val skillStats = stats[skillName] ?: return 0
        return skillStats[methodName] ?: 0
    }
}
