package com.lucdre.idleskills.cards.domain

import com.lucdre.idleskills.inventory.domain.ItemType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Requirement for upgrading a card.
 */
data class UpgradeRequirement(
    val itemType: ItemType,
    val quantity: Int
)

/**
 * Central registry for card upgrade costs.
 */
@Singleton
class CardUpgradeRegistry @Inject constructor() {

    /**
     * Map of [CardType] to a map of [Level] -> [Requirements].
     */
    private val upgradeCosts: Map<CardType, Map<Int, List<UpgradeRequirement>>> = mapOf(
        CardType.WOODCUTTING_CARD to mapOf(
            1 to listOf(
                UpgradeRequirement(ItemType.NORMAL_LOGS, 500),
                UpgradeRequirement(ItemType.OAK_LOGS, 250),
                UpgradeRequirement(ItemType.WILLOW_LOGS, 250)
            )
        ),
        CardType.MINING_CARD to mapOf(
            1 to listOf(
                UpgradeRequirement(ItemType.COPPER_ORE, 250),
                UpgradeRequirement(ItemType.TIN_ORE, 250),
                UpgradeRequirement(ItemType.IRON_ORE, 250),
                UpgradeRequirement(ItemType.COAL_ORE, 250)
            )
        ),
        CardType.FISHING_CARD to mapOf(
            1 to listOf(
                UpgradeRequirement(ItemType.RAW_SHRIMP, 500),
                UpgradeRequirement(ItemType.RAW_SARDINE, 250),
                UpgradeRequirement(ItemType.RAW_ANCHOVY, 250)
            )
        )
    )

    /**
     * Retrieves the requirements for upgrading a card to the next level.
     * 
     * @param cardType The type of card.
     * @param currentLevel The current level of the card.
     * @return List of requirements, or empty if no further upgrades defined.
     */
    fun getRequirements(cardType: CardType, currentLevel: Int): List<UpgradeRequirement> {
        return upgradeCosts[cardType]?.get(currentLevel) ?: emptyList()
    }
}
