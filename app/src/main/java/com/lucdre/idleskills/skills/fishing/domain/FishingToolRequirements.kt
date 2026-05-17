package com.lucdre.idleskills.skills.fishing.domain

import com.lucdre.idleskills.cards.domain.CardType

/**
 * Utility class that defines which fishing tool type is required for each fish.
 *
 */
object FishingToolRequirements {

    /**
     * Maps fish names to their required card type.
     */
    private val fishToCardTypeMap = mapOf(
        // Fishing net
        "Sardine" to CardType.FISHING_NET,
        "Anchovy" to CardType.FISHING_NET,

        // Fishing rod
        "Trout" to CardType.FISHING_ROD,
        "Salmon" to CardType.FISHING_ROD,

        // Harpoon
        "Tuna" to CardType.FISHING_HARPOON,
        "Swordfish" to CardType.FISHING_HARPOON,
        "Shark" to CardType.FISHING_HARPOON,

        // Lobster cage
        "Lobster" to CardType.FISHING_LOBSTER_CAGE,

        // Cheat fish - default to fishing rod
        "Cheat Fish" to CardType.FISHING_ROD
    )

    /**
     * Gets the required card type for a specific fish.
     *
     * @param fishName The name of the fish/training method
     * @return The required [CardType], or null if no specific requirement
     */
    fun getRequiredCardType(fishName: String): CardType? {
        return fishToCardTypeMap[fishName]
    }
}
