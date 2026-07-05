package com.lucdre.idleskills.inventory.domain.usecase

import com.lucdre.idleskills.core.persistence.InventoryEntity
import com.lucdre.idleskills.inventory.domain.InventoryRepositoryInterface
import com.lucdre.idleskills.inventory.domain.Item
import com.lucdre.idleskills.inventory.domain.ItemRegistry
import com.lucdre.idleskills.inventory.domain.ItemType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Use case for observing the player's inventory.
 * Transforms database entities into domain models for the UI.
 */
class GetInventoryUseCase @Inject constructor(
    private val repository: InventoryRepositoryInterface,
    private val itemRegistry: ItemRegistry
) {
    /**
     * Returns a Flow of the player's inventory items.
     * Items are mapped to domain [Item] objects and sorted by discovery time via the repository.
     */
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<Item>> {
        return repository.observeItems()
            .sample(500.milliseconds)
            .map { entities ->
                entities.mapNotNull { it.toDomain() }
            }
    }

    /**
     * Extension to map [InventoryEntity] to domain [Item].
     * Fetches metadata from [ItemRegistry].
     */
    private fun InventoryEntity.toDomain(): Item? {
        val type = ItemType.fromId(itemId) ?: return null
        val metadata = itemRegistry.getMetadata(type)
        return Item(type = type, quantity = quantity, metadata = metadata)
    }
}
