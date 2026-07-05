package com.lucdre.idleskills.inventory.domain

import androidx.compose.runtime.Immutable

/**
 * A stackable item in the player's inventory.
 * Combines [ItemType] with [ItemMetadata].
 *
 * @property type The unique type of the item.
 * @property quantity The number of items in the stack.
 * @property metadata Display information for the item.
 */
@Immutable
data class Item(
    val type: ItemType,
    val quantity: Int,
    val metadata: ItemMetadata
)
