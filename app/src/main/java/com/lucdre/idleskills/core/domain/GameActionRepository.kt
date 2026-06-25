package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.core.persistence.InventoryEntity
import com.lucdre.idleskills.core.persistence.ProgressApplicationDao
import com.lucdre.idleskills.inventory.domain.ItemType
import com.lucdre.idleskills.skills.domain.skill.LevelCalculator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameActionRepository @Inject constructor(
    private val progressApplicationDao: ProgressApplicationDao
) : GameActionRepositoryInterface {

    override suspend fun applyOfflineProgress(
        skillName: String,
        xpAmount: Int,
        items: Map<ItemType, Int>,
        now: Long
    ) {
        val inventoryEntities = items.map { (type, qty) ->
            InventoryEntity(type.id, qty)
        }

        progressApplicationDao.applyOfflineProgress(
            skillName = skillName,
            xpAmount = xpAmount,
            items = inventoryEntities,
            now = now,
            xpCap = LevelCalculator.MAX_XP
        )
    }
}
