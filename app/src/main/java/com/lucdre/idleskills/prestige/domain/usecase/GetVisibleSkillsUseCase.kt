package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for retrieving skills that should be visible to the player.
 *
 * Visible skills are determined by:
 * - The initial skill selected at game start
 * - Skills unlocked through the skill tree progression
 * - If no initial skill is selected, returns empty list (game needs initial selection)
 *
 * @property skillRepository The repository for skill data.
 * @property prestigeRepository The repository for prestige data.
 */
class GetVisibleSkillsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val prestigeRepository: PrestigeRepositoryInterface
) {
    /**
     * Returns the list of skills that should be visible based on skill tree progress.
     *
     * @return A filtered list of skills unlocked through the skill tree.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val prestige = prestigeRepository.getPrestige()
        val unlockedSkillNames = prestige.skillTreeProgress.getUnlockedSkills()

        // If no skills are unlocked, return empty list (indicates need for initial selection)
        if (unlockedSkillNames.isEmpty()) {
            return emptyList()
        }

        return skills.filter { it.name in unlockedSkillNames }
    }

    /**
     * Observes the list of skills that should be visible based on skill tree progress.
     *
     * @return A Flow of filtered skills that updates when skills or prestige changes.
     */
    fun observeVisibleSkills(): Flow<List<Skill>> {
        return skillRepository.observeSkills().combine(
            prestigeRepository.observePrestige()
        ) { skills, prestige ->
            val unlockedSkillNames = prestige.skillTreeProgress.getUnlockedSkills()

            // If no skills are unlocked, return empty list
            if (unlockedSkillNames.isEmpty()) {
                emptyList()
            } else {
                skills.filter { it.name in unlockedSkillNames }
            }
        }
    }
}
