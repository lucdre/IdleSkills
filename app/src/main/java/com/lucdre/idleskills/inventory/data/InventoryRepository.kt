package com.lucdre.idleskills.inventory.data

import com.lucdre.idleskills.core.persistence.InventoryDao
import com.lucdre.idleskills.core.persistence.InventoryEntity
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.inventory.domain.ItemType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao
) : InventoryRepositoryInterface {

    override fun observeItems(): Flow<List<InventoryEntity>> {
        return inventoryDao.observeItems()
    }

    override suspend fun addItem(itemType: ItemType, quantity: Int) {
        inventoryDao.addItem(itemType.id, quantity)
    }

    override suspend fun consumeItem(itemType: ItemType, quantity: Int): Boolean {
        val affected = inventoryDao.decrementQuantity(itemType.id, quantity)
        return affected > 0
    }
}
