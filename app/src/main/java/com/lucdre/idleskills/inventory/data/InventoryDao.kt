package com.lucdre.idleskills.inventory.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for inventory items.
 */
@Dao
interface InventoryDao {
    /**
     * Observes all inventory items sorted by acquisition time.
     */
    @Query("SELECT * FROM inventory ORDER BY acquiredAt ASC")
    fun observeItems(): Flow<List<InventoryEntity>>

    /**
     * Gets all inventory items.
     */
    @Query("SELECT * FROM inventory")
    suspend fun getItems(): List<InventoryEntity>

    /**
     * Inserts or updates an inventory item stack.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: InventoryEntity)

    /**
     * Decrements the quantity of an item.
     */
    @Query("UPDATE inventory SET quantity = quantity - :amount WHERE itemId = :itemId AND quantity >= :amount")
    suspend fun decrementQuantity(itemId: Int, amount: Int): Int

    /**
     * Adds an item stack, setting the acquisition timestamp only on the initial insert.
     */
    @Query("""
        INSERT INTO inventory (itemId, quantity, acquiredAt) 
        VALUES (:itemId, :amount, :acquiredAt)
        ON CONFLICT(itemId) DO UPDATE SET quantity = quantity + excluded.quantity
    """)
    suspend fun addItem(itemId: Int, amount: Int, acquiredAt: Long = System.currentTimeMillis())

    /**
     * Clears the entire player inventory.
     */
    @Query("DELETE FROM inventory")
    suspend fun clearInventory()
}
