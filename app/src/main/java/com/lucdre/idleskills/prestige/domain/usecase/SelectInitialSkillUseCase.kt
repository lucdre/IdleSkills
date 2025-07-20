package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.InitialSkillConfig
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.prestige.domain.skilltree.SkillTreeProgress
import javax.inject.Inject

/**
 * Use case for selecting the initial skill when starting a fresh game.
 *
 * @property prestigeRepository The repository for prestige data.
 */
class SelectInitialSkillUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
) {
    /**
     * Selects the initial skill for a fresh game.
     *
     * @param skillName The name of the skill to select as the initial skill.
     * @return True if selection was successful, false if invalid skill or game not fresh.
     */
    suspend operator fun invoke(skillName: String): Boolean {
        val currentPrestige = prestigeRepository.getPrestige()

        // Can only select initial skill if none is selected yet
        if (currentPrestige.skillTreeProgress.selectedInitialSkill != null) {
            return false
        }

        // Validate skill is available for initial selection
        if (skillName !in InitialSkillConfig.availableStartingSkills) {
            return false
        }

        // Update prestige with selected initial skill
        val updatedSkillTreeProgress = currentPrestige.skillTreeProgress.copy(
            selectedInitialSkill = skillName
        )

        val updatedPrestige = currentPrestige.copy(
            skillTreeProgress = updatedSkillTreeProgress
        )

        prestigeRepository.updatePrestige(updatedPrestige)
        return true
    }
}
