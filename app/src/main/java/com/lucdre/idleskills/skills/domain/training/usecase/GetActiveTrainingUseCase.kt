package com.lucdre.idleskills.skills.domain.training.usecase

import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe the current active training state.
 */
class GetActiveTrainingUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface
) {
    /**
     * Observes the active training state (skill and method).
     */
    operator fun invoke(): Flow<ActiveTraining?> {
        return skillRepository.observeActiveTraining()
    }
}
