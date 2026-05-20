package com.lucdre.idleskills.loot.domain.usecase

import com.lucdre.idleskills.loot.domain.LootBox
import com.lucdre.idleskills.loot.domain.LootRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing all owned loot boxes.
 */
class ObserveLootBoxCountUseCase @Inject constructor(
    private val lootRepository: LootRepositoryInterface
) {
    /**
     * @return A flow of the list of all loot boxes.
     */
    operator fun invoke(): Flow<List<LootBox>> {
        return lootRepository.observeLootBoxes()
    }
}
