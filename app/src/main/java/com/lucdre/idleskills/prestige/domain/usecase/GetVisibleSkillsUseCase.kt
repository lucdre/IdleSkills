package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import javax.inject.Inject

/**
 * Use case for retrieving skills that should be visible to the player.
 *
 * Filters the complete skill list based on the player's current prestige level.
 * Controls the progressive unlocking of skills through the prestige system.
 *
 * @property skillRepository The repository for skill data.
 * @property prestigeRepository The repository for prestige data.
 */
class GetVisibleSkillsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val prestigeRepository: PrestigeRepositoryInterface
){
    /**
     * Returns the list of skills that should be visible based on prestige level.
     *
     * - Prestige level 0: Only Woodcutting, Mining, and Fishing are visible
     * - Prestige level 1: Adds Firemaking, Smelting, Cooking
     *
     * @return A filtered list of skills appropriate for the current prestige level.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val prestige = prestigeRepository.getPrestige()

        return when (prestige.level) {
            0 -> {
                skills.filter { it.name in listOf("Woodcutting", "Mining", "Fishing") }
            }
            else -> {
                //TODO after first prestige all skills visible, so far.
                skills
            }
        }
    }
}