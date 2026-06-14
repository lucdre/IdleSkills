package com.lucdre.idleskills.cards.domain

import androidx.compose.runtime.Immutable
import com.lucdre.idleskills.R

/**
 * Represents a card that provides bonuses to skill training.
 *
 * @property name The display name of the card.
 * @property type The type/category of the card.
 * @property level The current level of the card.
 * @property quantity Number of copies owned of this specific card level.
 * @property efficiencyBonus The efficiency bonus provided by this card (e.g., 0.1f for 10% bonus).
 * @property iconResId The resource ID of the card's icon, which can change based on level.
 */
@Immutable
data class Card(
    val name: String,
    val type: CardType,
    val level: Int = 1,
    val quantity: Int = 1,
    val efficiencyBonus: Float = 0f,
    val iconResId: Int = R.drawable.ic_tree // Default icon
) {
    /**
     * @return The number of cards required to upgrade to the next level.
     */
    fun getUpgradeRequirement(): Int {
        return type.getUpgradeRequirement(level)
    }

    /**
     * @return True if the card can be upgraded based on current quantity.
     */
    fun canUpgrade(): Boolean {
        return quantity >= getUpgradeRequirement()
    }

    /**
     * Creates an upgraded version of this card.
     * Reduces quantity by the upgrade requirement and increases level/bonus.
     *
     * @return A new Card instance with upgraded stats.
     */
    fun createUpgrade(): Card {
        return copy(
            level = level + 1,
            quantity = quantity - getUpgradeRequirement(),
            efficiencyBonus = efficiencyBonus + type.bonusPerLevel
        )
    }
}
