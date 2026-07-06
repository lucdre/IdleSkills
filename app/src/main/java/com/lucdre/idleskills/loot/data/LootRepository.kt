package com.lucdre.idleskills.loot.data

import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistence-based implementation of the [LootRepositoryInterface].
 */
@Singleton
class LootRepository @Inject constructor(
    private val lootBoxDao: LootBoxDao
) : LootRepositoryInterface {

    override fun observeLootBoxes(): Flow<List<LootBox>> = lootBoxDao.observeLootBoxes()
        .map { entities ->
            entities.mapNotNull { 
                val skill = SkillType.fromString(it.skillName)
                if (skill != null) LootBox(skill, it.count) else null
            }
        }
        .flowOn(Dispatchers.IO)

    override suspend fun collectLootBox(skill: SkillType) = withContext(Dispatchers.IO) {
        lootBoxDao.updateLootBoxCount(skill.name, 1)
    }

    override suspend fun consumeLootBox(skill: SkillType): Boolean = withContext(Dispatchers.IO) {
        val affected = lootBoxDao.decrementLootBoxCount(skill.name)
        affected > 0
    }
}
