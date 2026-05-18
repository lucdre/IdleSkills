package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Use case for checking if the player meets the requirements to prestige.
 *
 * Validates prestige requirements based on current skill levels.
 * A player can prestige if they have reached at least one skill at level 99.
 *
 * @property skillRepository The repository for skill data.
 */
class CheckPrestigeRequirementsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface
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
        
        // Check if any skill has reached level 99
        return skills.any { it.level >= 99 }
    }
}
