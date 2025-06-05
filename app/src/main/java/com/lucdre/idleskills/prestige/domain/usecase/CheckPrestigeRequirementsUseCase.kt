package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeConfig
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Use case for checking if the player meets the requirements to prestige.
 *
 * Validates prestige requirements based on current prestige level and skill levels.
 * Requirements vary depending on the current prestige level.
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
     * Requirements by prestige level: See [PrestigeConfig]
     *
     * @return True if prestige requirements are met, false otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val currentPrestige = prestigeRepository.getPrestige()
        val skills = skillRepository.getSkills()

        // Check if this prestige level exists in config
        val requiredSkills = PrestigeConfig.getRequiredSkills(currentPrestige.level)
        val requiredLevel = PrestigeConfig.getRequiredLevel(currentPrestige.level)

        // If no required skills, can't prestige further (no next level defined)
        // Will need more checks when more conditions have to be met
        if (requiredSkills.isEmpty()) return false

        return requiredSkills.all { skillName ->
            skills.find { it.name == skillName }?.level?.let { it >= requiredLevel } == true
        }
    }
}