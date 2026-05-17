package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.profile.domain.RegionConfig
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

//TODO maybe move
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
     * Returns the list of skills that should be visible based on skill tree progress
     * and the current region.
     *
     * @return A filtered list of skills unlocked through the skill tree available in the region.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val prestige = prestigeRepository.getPrestige()
        val currentRegion = prestige.playerProfile.currentRegion
        val regionSkillNames = RegionConfig.getSkillsForRegion(currentRegion)

        // Only show skills that are defined for the current region
        return skills.filter { it.name in regionSkillNames }
    }

    /**
     * Observes the list of skills that should be visible based on current region.
     *
     * @return A Flow of filtered skills that updates when skills or player region changes.
     */
    fun observeVisibleSkills(): Flow<List<Skill>> {
        return skillRepository.observeSkills().combine(
            prestigeRepository.observePrestige()
        ) { skills, prestige ->
            val currentRegion = prestige.playerProfile.currentRegion
            val regionSkillNames = RegionConfig.getSkillsForRegion(currentRegion)

            skills.filter { it.name in regionSkillNames }
        }
    }
}
