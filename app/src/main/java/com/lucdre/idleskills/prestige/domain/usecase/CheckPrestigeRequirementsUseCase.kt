package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Checks prestige requirements.
 */
class CheckPrestigeRequirementsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface
) {
    /**
     * @return True if the player can currently prestige.
     */
    suspend operator fun invoke(): Boolean {
        val skills = skillRepository.getSkills()
        
        // Check if any skill has reached level 99
        return skills.any { it.level >= 99 }
    }
}
