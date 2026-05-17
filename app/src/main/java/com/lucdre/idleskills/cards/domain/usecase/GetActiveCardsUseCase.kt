package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to retrieve all owned cards relevant to a specific skill and training method.
 *
 * It filters cards based on:
 * 1. Skill compatibility (e.g., Woodcutting cards for Woodcutting skill).
 * 2. Training method compatibility (e.g., Fishing Rod card only for Rod Fishing methods).
 */
class GetActiveCardsUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface,
    private val trainingMethodRepository: TrainingMethodRepositoryInterface
) {
    operator fun invoke(skillName: String, methodName: String?): Flow<List<Card>> {
        return cardRepository.getCardsForSkill(skillName).map { cards ->
            if (methodName == null) {
                // If no method, return all cards for the skill (e.g., for general display)
                cards
            } else {
                // Find the training method to get its required card type
                val method = trainingMethodRepository.getTrainingMethodsForSkill(skillName)
                    .find { it.name == methodName }

                val requiredType = method?.requiredCardType

                if (requiredType == null) {
                    cards
                } else {
                    cards.filter { it.type == requiredType }
                }
            }
        }
    }
}
