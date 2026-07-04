package com.lucdre.idleskills.cards.domain

import com.lucdre.idleskills.inventory.domain.Item
import javax.inject.Inject

/**
 * Responsible for card-related calculations.
 * 
 * @property upgradeRegistry Registry for card upgrade costs.
 */
class CardCalculator @Inject constructor(
    private val upgradeRegistry: CardUpgradeRegistry
) {

    /**
     * @param cardType The type of card.
     * @param level The current level of the card.
     * @return The resources required to upgrade to the next level.
     */
    fun getUpgradeRequirements(cardType: CardType, level: Int): List<UpgradeRequirement> {
        return upgradeRegistry.getRequirements(cardType, level)
    }

    /**
     * @param card The current card.
     * @param inventoryItems Current list of items in the player's inventory.
     * @return True if the card can be upgraded based on inventory resources.
     */
    fun canUpgrade(card: Card, inventoryItems: List<Item>): Boolean {
        val requirements = getUpgradeRequirements(card.type, card.level)
        if (requirements.isEmpty()) return false
        
        return requirements.all { req ->
            val owned = inventoryItems.find { it.type == req.itemType }?.quantity ?: 0
            owned >= req.quantity
        }
    }

    /**
     * @param card The current card.
     * @return The efficiency bonus for the next level.
     */
    fun getNextLevelBonus(card: Card): Float {
        return card.efficiencyBonus + card.type.bonusPerLevel
    }
}
