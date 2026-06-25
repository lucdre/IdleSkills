package com.lucdre.idleskills.inventory.domain

import androidx.compose.runtime.Immutable

/**
 * A stackable item in the player's inventory.
 */
@Immutable
data class Item(
    val type: ItemType,
    val quantity: Int
)
