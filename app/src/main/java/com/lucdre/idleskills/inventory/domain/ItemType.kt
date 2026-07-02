package com.lucdre.idleskills.inventory.domain

import com.lucdre.idleskills.R

/**
 * Unique identifiers for all items in the game.
 * Uses integer IDs for efficient database storage and lookups.
 *
 * @property id Unique integer ID for the database.
 * @property displayName Human-readable name.
 * @property iconResId Resource ID for the item's icon.
 */
enum class ItemType(
    val id: Int,
    val displayName: String,
    val iconResId: Int
) {
    // Woodcutting (1000-1999)
    NORMAL_LOGS(1001, "Logs", R.drawable.ic_tree), // TODO: Replace with woodcutting icons
    OAK_LOGS(1002, "Oak Logs", R.drawable.ic_tree),
    WILLOW_LOGS(1003, "Willow Logs", R.drawable.ic_tree),
    MAPLE_LOGS(1004, "Maple Logs", R.drawable.ic_tree),
    YEW_LOGS(1005, "Yew Logs", R.drawable.ic_tree),
    MAGIC_LOGS(1006, "Magic Logs", R.drawable.ic_tree),

    // Mining (2000-2999)
    COPPER_ORE(2001, "Copper Ore", R.drawable.ic_tree), // TODO: Replace with mining icons
    TIN_ORE(2002, "Tin Ore", R.drawable.ic_tree),
    IRON_ORE(2003, "Iron Ore", R.drawable.ic_tree),
    COAL(2004, "Coal", R.drawable.ic_tree),
    MITHRIL_ORE(2005, "Mithril Ore", R.drawable.ic_tree),
    ADAMANT_ORE(2006, "Adamant Ore", R.drawable.ic_tree),
    RUNE_ORE(2007, "Rune Ore", R.drawable.ic_tree),
    DRAGON_ORE(2008, "Dragon Ore", R.drawable.ic_tree),

    // Fishing (3000-3999)
    RAW_SHRIMP(3001, "Raw Shrimp", R.drawable.ic_tree), // TODO: Replace with fishing icons
    RAW_SARDINE(3002, "Raw Sardine", R.drawable.ic_tree),
    RAW_ANCHOVY(3003, "Raw Anchovy", R.drawable.ic_tree),
    RAW_TROUT(3004, "Raw Trout", R.drawable.ic_tree),
    RAW_SALMON(3005, "Raw Salmon", R.drawable.ic_tree),
    RAW_TUNA(3006, "Raw Tuna", R.drawable.ic_tree),
    RAW_LOBSTER(3007, "Raw Lobster", R.drawable.ic_tree),
    RAW_SWORDFISH(3008, "Raw Swordfish", R.drawable.ic_tree),
    RAW_SHARK(3009, "Raw Shark", R.drawable.ic_tree);

    companion object {
        fun fromId(id: Int): ItemType? = entries.find { it.id == id }
    }
}
