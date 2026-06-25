package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.inventory.domain.ItemType

/**
 * Interface for multi-domain atomic actions.
 */
interface GameActionRepositoryInterface {
    /**
     * Apply offline gains.
     */
    suspend fun applyOfflineProgress(
        skillName: String,
        xpAmount: Int,
        items: Map<ItemType, Int>,
        now: Long
    )
}
