package com.lucdre.idleskills.inventory.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Index

/**
 * Database entity representing stackable items in the inventory.
 *
 * @property itemId Unique item identifier (mapped to ItemType enum ID).
 * @property quantity The current quantity owned.
 * @property acquiredAt Epoch millisecond timestamp when the item was first acquired.
 */
@Entity(
    tableName = "inventory",
    indices = [Index(value = ["acquiredAt"])]
)
data class InventoryEntity(
    @PrimaryKey val itemId: Int,
    val quantity: Int,
    val acquiredAt: Long = System.currentTimeMillis()
)
