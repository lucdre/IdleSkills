package com.lucdre.idleskills.cards.data

import android.util.Log
import com.lucdre.idleskills.R
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.core.persistence.CardDao
import com.lucdre.idleskills.core.persistence.CardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that manages the player's card collection.
 */
@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao
) : CardRepositoryInterface {

    // Initial set of cards
    private val initialCards = listOf(
        Card("Bronze Axe", CardType.WOODCUTTING_AXE, 1, 1, 0.00f, R.drawable.ic_tree),
        Card("Bronze Pickaxe", CardType.MINING_PICKAXE, 1, 1, 0.00f, R.drawable.ic_tree),
        Card("Small Fishing Net", CardType.FISHING_NET, 1, 1, 0.00f, R.drawable.ic_tree),
        Card("Fishing Rod", CardType.FISHING_ROD, 1, 1, 0.00f, R.drawable.ic_tree),
        Card("Harpoon", CardType.FISHING_HARPOON, 1, 1, 0.00f, R.drawable.ic_tree),
        Card("Lobster Cage", CardType.FISHING_LOBSTER_CAGE, 1, 1, 0.00f, R.drawable.ic_tree)
    )

    override fun getOwnedCards(): Flow<List<Card>> = flow {
        // Check if DB is empty and initialize if it is.
        val currentEntities = cardDao.observeAllCards().map { entities ->
            if (entities.isEmpty()) {
                initializeDatabase()
                initialCards
            } else {
                // Sort by the order defined in CardType enum
                entities.map { it.toDomain() }
                    .sortedBy { it.type.ordinal }
            }
        }
        emitAll(currentEntities)
    }

    private suspend fun initializeDatabase() {
        val entities = initialCards.map { it.toEntity() }
        cardDao.insertAll(entities)
        Log.d("CardRepository", "Initialized card database with ${entities.size} default cards.")
    }

    override fun getCardsForSkill(skillName: String): Flow<List<Card>> {
        return getOwnedCards().map { cards ->
            cards.filter { it.type.skillName == skillName }
        }
    }

    override suspend fun updateCard(card: Card) {
        cardDao.insertOrUpdate(card.toEntity())
    }

    override suspend fun addCards(cardType: CardType, quantity: Int) {
        val currentEntity = cardDao.getCardByType(cardType.name)
        if (currentEntity != null) {
            cardDao.insertOrUpdate(currentEntity.copy(quantity = currentEntity.quantity + quantity))
        } else {
            // If not in DB, find in initialCards and insert
            val initial = initialCards.find { it.type == cardType }
            if (initial != null) {
                cardDao.insertOrUpdate(initial.toEntity().copy(quantity = quantity))
            }
        }
    }

    private fun Card.toEntity(): CardEntity {
        return CardEntity(
            cardType = type.name,
            name = name,
            quantity = quantity,
            level = level,
            efficiencyBonus = efficiencyBonus,
            iconResId = iconResId
        )
    }

    private fun CardEntity.toDomain(): Card {
        return Card(
            name = name,
            type = CardType.valueOf(cardType),
            level = level,
            quantity = quantity,
            efficiencyBonus = efficiencyBonus,
            iconResId = iconResId
        )
    }
}
