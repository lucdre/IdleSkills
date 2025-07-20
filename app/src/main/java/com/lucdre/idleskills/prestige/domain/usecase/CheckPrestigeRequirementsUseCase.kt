package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Use case for checking if the player meets the requirements to prestige.
 *
 * Validates prestige requirements based on current prestige level and skill levels.
 * A player can prestige if they have reached 1 skill 99 and earn a Prestige Point with each skill at 99.
 *
 * @property skillRepository The repository for skill data.
 * @property prestigeRepository The repository for prestige data.
 */
class CheckPrestigeRequirementsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val prestigeRepository: PrestigeRepositoryInterface
) {
    /**
     * Checks if the player can prestige based on their current state.
     *
     * Requirements: At least one skill must be at level 99.
     *
     * @return True if prestige requirements are met, false otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val skills = skillRepository.getSkills()
        val currentPrestige = prestigeRepository.getPrestige()

        // Get currently unlocked skills based on skill tree progress
        val unlockedSkills = currentPrestige.skillTreeProgress.getUnlockedSkills()

        // Check if any unlocked skill has reached level 99
        return skills.any { skill ->
            unlockedSkills.contains(skill.name) && skill.level >= 99
        }
    }
}
