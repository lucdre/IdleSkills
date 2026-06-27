package com.lucdre.idleskills.cards.domain

import javax.inject.Inject

/**
 * Responsible for card-related calculations.
 */
class CardCalculator @Inject constructor() {

    /**
     * @param level The current level of the card.
     * @return The number of cards required to upgrade to the next level.
     */
    fun getUpgradeRequirement(level: Int): Int {
        return level * 10
    }

    /**
     * @param card The current card.
     * @return True if the card can be upgraded based on its quantity.
     */
    fun canUpgrade(card: Card): Boolean {
        return card.quantity >= getUpgradeRequirement(card.level)
    }

    /**
     * @param card The current card.
     * @return The efficiency bonus for the next level.
     */
    fun getNextLevelBonus(card: Card): Float {
        return card.efficiencyBonus + card.type.bonusPerLevel
    }

    /**
     * Creates an upgraded version of the provided card.
     * 
     * @param card The card to upgrade.
     * @return A new [Card] instance with upgraded stats.
     * @throws IllegalArgumentException if the card does not meet upgrade requirements.
     */
    fun createUpgrade(card: Card): Card {
        require(canUpgrade(card)) { 
            "Insufficient quantity to upgrade card: ${card.quantity}/${getUpgradeRequirement(card.level)}" 
        }

        return card.copy(
            level = card.level + 1,
            quantity = card.quantity - getUpgradeRequirement(card.level),
            efficiencyBonus = getNextLevelBonus(card)
        )
    }
}
