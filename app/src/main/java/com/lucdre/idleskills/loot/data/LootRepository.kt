package com.lucdre.idleskills.loot.data

import com.lucdre.idleskills.core.persistence.LootBoxDao
import com.lucdre.idleskills.core.persistence.LootBoxEntity
import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistence-based implementation of the [LootRepositoryInterface].
 */
@Singleton
class LootRepository @Inject constructor(
    private val lootBoxDao: LootBoxDao
) : LootRepositoryInterface {

    override fun observeLootBoxes(): Flow<List<LootBox>> = lootBoxDao.observeLootBoxes().map { entities ->
        entities.mapNotNull { 
            val skill = SkillType.fromString(it.skillName)
            if (skill != null) LootBox(skill, it.count) else null
        }
    }

    override suspend fun collectLootBox(skillName: String) {
        val currentBoxes = lootBoxDao.observeLootBoxes().first()
        val existing = currentBoxes.find { it.skillName == skillName }
        val newCount = (existing?.count ?: 0) + 1
        lootBoxDao.updateLootBox(LootBoxEntity(skillName, newCount))
    }

    override suspend fun consumeLootBox(skillName: String): Boolean {
        val currentBoxes = lootBoxDao.observeLootBoxes().first()
        val existing = currentBoxes.find { it.skillName == skillName }
        val count = existing?.count ?: 0
        
        return if (count > 0) {
            lootBoxDao.updateLootBox(LootBoxEntity(skillName, count - 1))
            true
        } else {
            false
        }
    }
}
