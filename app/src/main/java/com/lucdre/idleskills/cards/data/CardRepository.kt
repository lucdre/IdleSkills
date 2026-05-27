package com.lucdre.idleskills.cards.data

import android.util.Log
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.cards.domain.CardType
import com.lucdre.idleskills.core.persistence.CardDao
import com.lucdre.idleskills.core.persistence.CardEntity
import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that manages the player's card collection.
 */
@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao
) : CardRepositoryInterface {

    override fun getOwnedCards(): Flow<List<Card>> {
        return cardDao.observeAllCards()
            .onStart {
                // TODO potential Database Callback
                val existing = cardDao.getCardByType(CardType.WOODCUTTING_AXE.name)
                if (existing == null) {
                    initializeDatabase()
                }
            }
            .map { entities ->
                entities.map { it.toDomain() }
                    .sortedBy { it.type.ordinal }
            }
    }

    private suspend fun initializeDatabase() {
        val initialEntities = CardType.entries.map { type ->
            Card(
                name = type.displayName,
                type = type,
                level = 1,
                quantity = 1,
                efficiencyBonus = type.initialEfficiencyBonus,
                iconResId = type.iconResId
            ).toEntity()
        }
        cardDao.insertAll(initialEntities)
        Log.d("CardRepository", "Initialized card database with ${initialEntities.size} default cards.")
    }

    override fun getCardsForSkill(skill: SkillType): Flow<List<Card>> {
        return getOwnedCards().map { cards ->
            cards.filter { it.type.skill == skill }
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
            // Use metadata from CardType to create the entity if it doesn't exist
            val newEntity = CardEntity(
                cardType = cardType.name,
                name = cardType.displayName,
                quantity = quantity,
                level = 1,
                efficiencyBonus = cardType.initialEfficiencyBonus,
                iconResId = cardType.iconResId
            )
            cardDao.insertOrUpdate(newEntity)
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
