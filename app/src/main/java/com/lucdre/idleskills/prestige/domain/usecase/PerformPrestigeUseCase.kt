package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.usecase.ResetSkillsUseCase
import javax.inject.Inject

/**
 * Use case for performing a prestige operation.
 *
 * Handles the prestige process: checking requirements, resetting skills,
 * and incrementing the prestige level.
 *
 * @property prestigeRepository The repository for prestige data.
 * @property getPrestigeStateUseCase Use case to see the complete state of the prestige.
 * @property resetSkillsUseCase Use case to reset all skills to initial state.
 */
class PerformPrestigeUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
    private val getPrestigeStateUseCase: GetPrestigeStateUseCase,
    private val resetSkillsUseCase: ResetSkillsUseCase
){
    /**
     * Performs the prestige operation if requirements are met.
     *
     * Checks if the player can prestige, resets all skills to level 1 with 0 XP,
     * cancels all the training and increments the prestige level.
     *
     * @param resetTrainingState Resets all the progress of the skills.
     *
     * @return True if prestige was successful, false if requirements weren't met.
     */
    suspend operator fun invoke(resetTrainingState: () -> Unit = {}): Boolean {
        val prestigeState = getPrestigeStateUseCase()

        if (!prestigeState.canPrestige) return false

        // Reset all training state to fresh start
        resetTrainingState()

        // Reset ALL skills to level 1, 0 XP
        resetSkillsUseCase()

        // Increment prestige level
        prestigeRepository.updatePrestige(prestigeState.copy(level = prestigeState.level + 1))

        return true
    }
}