package com.lucdre.idleskills.cards.domain

/**
 * Categories of cards in the game.
 *
 * Naming convention: SKILL_TOOL
 */
enum class CardType(val skillName: String) {
    WOODCUTTING_AXE("Woodcutting"),
    MINING_PICKAXE("Mining"),
    FISHING_NET("Fishing"),
    FISHING_ROD("Fishing"),
    FISHING_HARPOON("Fishing"),
    FISHING_LOBSTER_CAGE("Fishing")
}
