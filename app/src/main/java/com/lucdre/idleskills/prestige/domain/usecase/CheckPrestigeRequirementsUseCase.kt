package com.lucdre.idleskills.prestige.domain.usecase

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
     * Requirements by prestige level:
     * - Level 0 → 1: Woodcutting, Mining, and Fishing must be level 99
     * - Level 1+: No further prestiges implemented yet
     *
     * @return True if prestige requirements are met, false otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val currentPrestige = prestigeRepository.getPrestige()
        val skills = skillRepository.getSkills()

        // Prestige 1 requirements:
        // - Level 99: WC, Mining, Fishing
        return when (currentPrestige.level) {
            0 -> {
                val woodcutting = skills.find { it.name == "Woodcutting" }
                val mining = skills.find { it.name == "Mining" }
                val fishing = skills.find { it.name == "Fishing" }

                woodcutting?.level == 99 && mining?.level == 99 && fishing?.level == 99
            }
            else -> false // No further prestiges implemented yet
        }
    }
}