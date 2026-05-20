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

    /**
     * Updates a specific card in the player's collection.
     *
     * @param card The card with updated data.
     */
    suspend fun updateCard(card: Card)

    /**
     * Adds a certain amount of cards to the player's collection.
     *
     * @param cardType The type of card to add.
     * @param quantity The number of cards to add.
     */
    suspend fun addCards(cardType: CardType, quantity: Int)
}
