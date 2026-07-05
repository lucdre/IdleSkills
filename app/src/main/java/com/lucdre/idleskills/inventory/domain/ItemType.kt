package com.lucdre.idleskills.inventory.domain

/**
 * Unique identifiers for all items in the game.
 * Uses integer IDs for efficient database storage and lookups.
 * Metadata is managed by [ItemRegistry].
 *
 * @property id Unique integer ID for the database.
 */
enum class ItemType(val id: Int) {
    // Woodcutting (1000-1999)
    NORMAL_LOGS(1001),
    OAK_LOGS(1002),
    WILLOW_LOGS(1003),
    MAPLE_LOGS(1004),
    YEW_LOGS(1005),
    MAGIC_LOGS(1006),

    // Mining (2000-2999)
    COPPER_ORE(2001),
    TIN_ORE(2002),
    IRON_ORE(2003),
    COAL_ORE(2004),
    MITHRIL_ORE(2005),
    ADAMANT_ORE(2006),
    RUNE_ORE(2007),
    DRAGON_ORE(2008),

    // Fishing (3000-3999)
    RAW_SHRIMP(3001),
    RAW_SARDINE(3002),
    RAW_ANCHOVY(3003),
    RAW_TROUT(3004),
    RAW_SALMON(3005),
    RAW_TUNA(3006),
    RAW_LOBSTER(3007),
    RAW_SWORDFISH(3008),
    RAW_SHARK(3009);

    companion object {
        private val idMap = entries.associateBy { it.id }

        /**
         * Returns the [ItemType] associated with the given [id].
         *
         * @param id The unique integer identifier of the item.
         * @return The matching [ItemType] or null if not found.
         */
        fun fromId(id: Int): ItemType? = idMap[id]
    }
}
