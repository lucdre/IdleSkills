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
}
