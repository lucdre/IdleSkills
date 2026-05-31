package com.lucdre.idleskills.inventory.domain

/**
 * Represents a stackable item in the player's inventory.
 */
data class Item(
    val type: ItemType,
    val quantity: Int
)
