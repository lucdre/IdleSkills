package com.lucdre.idleskills.inventory.data

import com.lucdre.idleskills.core.persistence.InventoryDao
import com.lucdre.idleskills.core.persistence.InventoryEntity
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.inventory.domain.ItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao
) : InventoryRepositoryInterface {

    override fun observeItems(): Flow<List<Item>> {
        return inventoryDao.observeItems().map { entities ->
            entities.mapNotNull { it.toDomain() }
        }
    }

    override suspend fun addItem(itemType: ItemType, quantity: Int) {
        inventoryDao.addItem(itemType.id, quantity)
    }

    override suspend fun consumeItem(itemType: ItemType, quantity: Int): Boolean {
        val affected = inventoryDao.decrementQuantity(itemType.id, quantity)
        return affected > 0
    }

    private fun InventoryEntity.toDomain(): Item? {
        val type = ItemType.fromId(itemId) ?: return null
        return Item(type = type, quantity = quantity)
    }
}
