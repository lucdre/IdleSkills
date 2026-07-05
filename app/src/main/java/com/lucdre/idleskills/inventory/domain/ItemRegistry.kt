package com.lucdre.idleskills.inventory.domain

import com.lucdre.idleskills.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central registry for all item metadata in the game.
 * Maps [ItemType] to [ItemMetadata].
 */
@Singleton
class ItemRegistry @Inject constructor() {

    private val registry: Map<ItemType, ItemMetadata> = mapOf(
        // Woodcutting (1000-1999)
        ItemType.NORMAL_LOGS to ItemMetadata("Logs", R.drawable.item_normal_logs),
        ItemType.OAK_LOGS to ItemMetadata("Oak Logs", R.drawable.ic_tree),
        ItemType.WILLOW_LOGS to ItemMetadata("Willow Logs", R.drawable.ic_tree),
        ItemType.MAPLE_LOGS to ItemMetadata("Maple Logs", R.drawable.ic_tree),
        ItemType.YEW_LOGS to ItemMetadata("Yew Logs", R.drawable.ic_tree),
        ItemType.MAGIC_LOGS to ItemMetadata("Magic Logs", R.drawable.ic_tree),

        // Mining (2000-2999)
        ItemType.COPPER_ORE to ItemMetadata("Copper Ore", R.drawable.ic_tree),
        ItemType.TIN_ORE to ItemMetadata("Tin Ore", R.drawable.ic_tree),
        ItemType.IRON_ORE to ItemMetadata("Iron Ore", R.drawable.ic_tree),
        ItemType.COAL_ORE to ItemMetadata("Coal", R.drawable.ic_tree),
        ItemType.MITHRIL_ORE to ItemMetadata("Mithril Ore", R.drawable.ic_tree),
        ItemType.ADAMANT_ORE to ItemMetadata("Adamant Ore", R.drawable.ic_tree),
        ItemType.RUNE_ORE to ItemMetadata("Rune Ore", R.drawable.ic_tree),
        ItemType.DRAGON_ORE to ItemMetadata("Dragon Ore", R.drawable.ic_tree),

        // Fishing (3000-3999)
        ItemType.RAW_SHRIMP to ItemMetadata("Raw Shrimp", R.drawable.ic_tree),
        ItemType.RAW_SARDINE to ItemMetadata("Raw Sardine", R.drawable.ic_tree),
        ItemType.RAW_ANCHOVY to ItemMetadata("Raw Anchovy", R.drawable.ic_tree),
        ItemType.RAW_TROUT to ItemMetadata("Raw Trout", R.drawable.ic_tree),
        ItemType.RAW_SALMON to ItemMetadata("Raw Salmon", R.drawable.ic_tree),
        ItemType.RAW_TUNA to ItemMetadata("Raw Tuna", R.drawable.ic_tree),
        ItemType.RAW_LOBSTER to ItemMetadata("Raw Lobster", R.drawable.ic_tree),
        ItemType.RAW_SWORDFISH to ItemMetadata("Raw Swordfish", R.drawable.ic_tree),
        ItemType.RAW_SHARK to ItemMetadata("Raw Shark", R.drawable.ic_tree)
    )

    /**
     * Retrieves metadata for a specific item type.
     *
     * @param type The [ItemType] to look up.
     * @return The [ItemMetadata] for the item, or a default fallback if not found.
     */
    fun getMetadata(type: ItemType): ItemMetadata {
        return registry[type] ?: ItemMetadata("Unknown Item", R.drawable.ic_tree)
    }
}
