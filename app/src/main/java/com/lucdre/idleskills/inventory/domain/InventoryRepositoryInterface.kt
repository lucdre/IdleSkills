package com.lucdre.idleskills.inventory.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing the player's inventory.
 */
interface InventoryRepositoryInterface {
    /**
     * Observes the list of all items in the inventory.
     */
    fun observeItems(): Flow<List<Item>>

    /**
     * Gets all items in the inventory.
     */
    suspend fun getItems(): List<Item>

    /**
     * Adds a quantity of an item to the inventory.
     */
    suspend fun addItem(itemType: ItemType, quantity: Int)

    /**
     * Resets the inventory by clearing all items.
     */
    suspend fun resetInventory()
}
