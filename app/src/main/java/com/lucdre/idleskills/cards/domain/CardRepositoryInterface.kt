package com.lucdre.idleskills.cards.domain

import com.lucdre.idleskills.skills.domain.skill.SkillType
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
    fun getCardsForSkill(skill: SkillType): Flow<List<Card>>

    /**
     * Adds a certain amount of cards to the player's collection.
     *
     * @param cardType The type of card to add.
     * @param quantity The number of cards to add.
     */
    suspend fun addCards(cardType: CardType, quantity: Int)

    /**
     * Adds multiple types of cards to the player's collection in a batch.
     *
     * @param cards A map of card types to the quantity to add.
     */
    suspend fun addCardsBatch(cards: Map<CardType, Int>)

    /**
     * Atomically upgrades a card.
     */
    suspend fun upgradeCard(card: Card, requirement: Int, nextLevel: Int, bonus: Float)
}
