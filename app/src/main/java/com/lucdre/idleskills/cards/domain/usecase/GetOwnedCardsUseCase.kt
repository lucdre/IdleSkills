package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing owned cards from the repository.
 */
class GetOwnedCardsUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface
) {
    operator fun invoke(): Flow<List<Card>> {
        return cardRepository.getOwnedCards()
    }
}
