package com.lucdre.idleskills.loot.data

import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of the [LootRepositoryInterface].
 */
@Singleton
class LootRepository @Inject constructor() : LootRepositoryInterface {

    private val _lootBoxes = MutableStateFlow<Map<String, Int>>(emptyMap())

    override fun observeLootBoxes(): Flow<List<LootBox>> = _lootBoxes.map { map ->
        map.map { (skill, count) -> LootBox(skill, count) }
    }

    override suspend fun collectLootBox(skillName: String) {
        _lootBoxes.update { current ->
            val count = current[skillName] ?: 0
            current + (skillName to (count + 1))
        }
    }

    override suspend fun consumeLootBox(skillName: String): Boolean {
        val currentMap = _lootBoxes.value
        val count = currentMap[skillName] ?: 0
        
        return if (count > 0) {
            _lootBoxes.update { it + (skillName to (count - 1)) }
            true
        } else {
            false
        }
    }
}
