package com.lucdre.idleskills.cards.data

import com.lucdre.idleskills.R
import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.cards.domain.CardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that manages the player's card collection.
 *
 * Provides basic level 1 cards for all starter skills.
 */
@Singleton
class CardRepository @Inject constructor() : CardRepositoryInterface {

    // Initial set of cards the player starts with
    private val _ownedCards = MutableStateFlow(
        listOf(
            Card("Bronze Axe", CardType.WOODCUTTING_AXE, 1, 5, 0.05f, R.drawable.ic_tree),
            Card("Bronze Pickaxe", CardType.MINING_PICKAXE, 1, 2, 0.05f, R.drawable.ic_tree),
            Card("Small Fishing Net", CardType.FISHING_NET, 1, 10, 0.05f, R.drawable.ic_tree),
            Card("Fishing Rod", CardType.FISHING_ROD, 1, 1, 0.05f, R.drawable.ic_tree),
            Card("Harpoon", CardType.FISHING_HARPOON, 1, 0, 0.05f, R.drawable.ic_tree),
            Card("Lobster Cage", CardType.FISHING_LOBSTER_CAGE, 1, 0, 0.05f, R.drawable.ic_tree)
        )
    )

    override fun getOwnedCards(): Flow<List<Card>> = _ownedCards

    override fun getCardsForSkill(skillName: String): Flow<List<Card>> {
        return _ownedCards.map { cards ->
            cards.filter { it.type.skillName == skillName }
        }
    }
}
