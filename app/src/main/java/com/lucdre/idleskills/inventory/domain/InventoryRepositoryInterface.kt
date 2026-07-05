package com.lucdre.idleskills.inventory.domain

import com.lucdre.idleskills.core.persistence.InventoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing the player's inventory.
 */
interface InventoryRepositoryInterface {
    /**
     * Observes the list of all items in the inventory as database entities.
     */
    fun observeItems(): Flow<List<InventoryEntity>>

    /**
     * Adds a quantity of an item to the inventory.
     */
    suspend fun addItem(itemType: ItemType, quantity: Int)

    /**
     * Consumes a quantity of an item from the inventory.
     * @return True if consumption was successful, false if insufficient quantity.
     */
    suspend fun consumeItem(itemType: ItemType, quantity: Int): Boolean

}
