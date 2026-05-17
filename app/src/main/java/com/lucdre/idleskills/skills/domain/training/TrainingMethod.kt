package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import kotlin.math.roundToInt

/**
 * Represents a training method that can be used to train a specific skill.
 *
 * @property skillName The name of the skill this training method is used for.
 * @property name The name of the training method.
 * @property xpPerAction The XP you get per action.
 * @property actionDurationMs The time the action takes to complete in milliseconds.
 * @property requiredLevel The required level to use the training method, defaults to 1.
 * @property requiredCardType The type of card required to train with this method, if any.
 */
data class TrainingMethod(
    val skillName: String,
    val name: String,
    val xpPerAction: Int,
    val actionDurationMs: Long,
    val requiredLevel: Int = 1,
    val requiredCardType: CardType? = null
) {
    /**
     * @return The XP per hour for this training method.
     */
    fun calculateXpPerHour(): Int {
        val actionsPerHour = (3600 * 1000) / actionDurationMs
        return (actionsPerHour * xpPerAction).toInt()
    }

    /**
     * @param cards List of cards that affect action speed through efficiency bonuses
     * @return The XP per hour for this training method with card bonuses applied.
     */
    fun calculateXpPerHour(cards: List<Card> = emptyList()): Int {
        val effectiveActionDuration = getEffectiveActionDuration(cards)
        val actionsPerHour = (3600 * 1000) / effectiveActionDuration
        return (actionsPerHour * xpPerAction).roundToInt()
    }

    fun getEffectiveActionDuration(cards: List<Card> = emptyList()): Float {
        val totalEfficiency = 1.0f + cards.sumOf { it.efficiencyBonus.toDouble() }.toFloat()
        return actionDurationMs / totalEfficiency
    }
}
