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
 * @property checkPrestigeRequirementsUseCase Use case to verify prestige requirements.
 * @property resetSkillsUseCase Use case to reset all skills to initial state.
 */
class PerformPrestigeUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
    private val checkPrestigeRequirementsUseCase: CheckPrestigeRequirementsUseCase,
    private val resetSkillsUseCase: ResetSkillsUseCase
){
    /**
     * Performs the prestige operation if requirements are met.
     *
     * Checks if the player can prestige, resets all skills to level 1 with 0 XP,
     * and increments the prestige level.
     *
     * @return True if prestige was successful, false if requirements weren't met.
     */
    suspend operator fun invoke(): Boolean {
        val canPrestige = checkPrestigeRequirementsUseCase()
        val currentPrestige = prestigeRepository.getPrestige()

        if (!canPrestige) return false

        // Reset ALL skills to level 1, 0 XP
        resetSkillsUseCase()

        // Increment prestige level
        prestigeRepository.updatePrestige(currentPrestige.copy(level = currentPrestige.level + 1))

        return true
    }
}