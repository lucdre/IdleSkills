package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.skills.domain.tools.Tool
import kotlin.math.roundToInt

/**
 * Represents a training method that can be used to train a specific skill.
 *
 * @property skillName The name of the skill this training method is used for.
 * @property name The name of the training method.
 * @property xpPerAction The XP you get per action.
 * @property actionDurationMs The time the action takes to complete in milliseconds.
 * @property requiredLevel The required level to use the training method, defaults to 1.
 */
data class TrainingMethod(
    val skillName: String,
    val name: String,
    val xpPerAction: Int,
    val actionDurationMs: Long,
    val requiredLevel: Int = 1
) {
    /**
     * @return The XP per hour for this training method.
     */
    fun calculateXpPerHour(): Int {
        val actionsPerHour = (3600 * 1000) / actionDurationMs
        return (actionsPerHour * xpPerAction).toInt()
    }

    /**
     * @param tool Optional tool that affects action speed through efficiency
     * @return The XP per hour for this training method with tool efficiency applied.
     */
    fun calculateXpPerHour(tool: Tool? = null): Int {
        val effectiveActionDuration = actionDurationMs / (tool?.efficiency ?: 1f)
        val actionsPerHour = (3600 * 1000) / effectiveActionDuration
        return (actionsPerHour * xpPerAction).roundToInt()
    }
}