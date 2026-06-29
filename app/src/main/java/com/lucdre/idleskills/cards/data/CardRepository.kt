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
                if (cardDao.getCount() == 0) {
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

    override suspend fun addCards(cardType: CardType, quantity: Int) {
        addCardsBatch(mapOf(cardType to quantity))
    }

    override suspend fun addCardsBatch(cards: Map<CardType, Int>) {
        val types = cards.keys.map { it.name }
        val existingEntities = cardDao.getCardsByTypes(types).associateBy { it.cardType }
        
        val updatedEntities = cards.map { (cardType, quantity) ->
            val currentEntity = existingEntities[cardType.name]
            currentEntity?.copy(quantity = currentEntity.quantity + quantity)
                ?: CardEntity(
                    cardType = cardType.name,
                    name = cardType.displayName,
                    quantity = quantity,
                    level = 1,
                    efficiencyBonus = cardType.initialEfficiencyBonus,
                    iconResId = cardType.iconResId
                )
        }
        cardDao.insertAll(updatedEntities)
    }

    override suspend fun upgradeCard(
        card: Card,
        requirement: Int,
        nextLevel: Int,
        bonus: Float
    ) {
        cardDao.upgradeCard(
            cardType = card.type.name,
            requirement = requirement,
            nextLevel = nextLevel,
            bonus = bonus
        )
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
