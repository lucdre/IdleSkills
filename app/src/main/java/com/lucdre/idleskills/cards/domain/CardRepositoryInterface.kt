package com.lucdre.idleskills.cards.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing card data.
 */
interface CardRepositoryInterface {
    /**
     * Retrieves all cards owned by the player.
     */
    fun getOwnedCards(): Flow<List<Card>>

    /**
     * Retrieves cards owned by the player for a specific skill.
     */
    fun getCardsForSkill(skillName: String): Flow<List<Card>>
}
