package com.lucdre.idleskills.cards.domain.usecase

import com.lucdre.idleskills.cards.domain.Card
import com.lucdre.idleskills.cards.domain.CardRepositoryInterface
import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.TrainingMethodType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case to retrieve all owned cards relevant to a specific skill and training method.
 *
 * It filters cards based on:
 * 1. Skill compatibility (e.g., Woodcutting cards for Woodcutting skill).
 * 2. Training method compatibility (e.g., Fishing Rod card only for Rod Fishing methods).
 *
 * @property cardRepository The card repository
 * @property trainingMethodRepository The training method repository
 * @property sessionRepository The session repository
 */
class GetActiveCardsUseCase @Inject constructor(
    private val cardRepository: CardRepositoryInterface,
    private val trainingMethodRepository: TrainingMethodRepositoryInterface,
    private val sessionRepository: SessionRepositoryInterface
) {
    operator fun invoke(skill: SkillType, methodType: TrainingMethodType?): Flow<List<Card>> {
        return combine(
            cardRepository.getCardsForSkill(skill),
            sessionRepository.observeCurrentRegion()
        ) { cards, region ->
            if (methodType == null) {
                // If no method, return all cards for the skill (e.g., for general display)
                cards
            } else {
                // Find the training method to get its required card type
                val method = trainingMethodRepository.getTrainingMethodsForSkill(skill, region)
                    .find { it.type == methodType }

                val requiredCardType = method?.requiredCardType
                
                if (requiredCardType == null) {
                    // Method doesn't benefit from any specific card type
                    emptyList()
                } else {
                    // Filter to only include cards of the required type
                    cards.filter { it.type == requiredCardType }
                }
            }
        }
    }
}
