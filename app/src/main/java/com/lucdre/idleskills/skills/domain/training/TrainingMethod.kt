package com.lucdre.idleskills.skills.domain.training

import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlin.math.roundToInt

/**
 * A training method for a specific skill.
 *
 * @property skill The skill this training method is used for.
 * @property name The name of the training method.
 * @property xpPerAction The XP you get per action.
 * @property actionDurationMs The time the action takes to complete in milliseconds.
 * @property requiredLevel The required level to use the training method, defaults to 1.
 * @property requiredCardType The type of card required to train with this method, if any.
 * @property availableRegions The list of regions where this training method is available.
 */
@Immutable
data class TrainingMethod(
    val skill: SkillType,
    val name: String,
    val xpPerAction: Int,
    val actionDurationMs: Long,
    val requiredLevel: Int = 1,
    val requiredCardType: CardType? = null,
    val availableRegions: List<Region> = emptyList(),
    val producedItemType: com.lucdre.idleskills.inventory.domain.ItemType? = null
) {
    /**
     * @param cards List of cards that affect action speed through efficiency bonuses
     * @return The XP per hour for this training method with card bonuses applied.
     */
    fun calculateXpPerHour(cards: List<Card> = emptyList()): Int {
        val effectiveActionDuration = getEffectiveActionDuration(cards)
        val actionsPerHour = (3600.0 * 1000.0) / effectiveActionDuration
        return (actionsPerHour * xpPerAction).roundToInt()
    }

    fun getEffectiveActionDuration(cards: List<Card> = emptyList()): Double {
        val totalEfficiency = 1.0 + cards.sumOf { it.efficiencyBonus.toDouble() }
        return actionDurationMs.toDouble() / totalEfficiency
    }
}
