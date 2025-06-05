package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeConfig
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
     * Requirements by prestige level: See [PrestigeConfig]
     *
     * @return A filtered list of skills appropriate for the current prestige level.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val prestige = prestigeRepository.getPrestige()
        val visibleSkillNames = PrestigeConfig.getVisibleSkills(prestige.level)

        return skills.filter { it.name in visibleSkillNames }
    }

    /**
     * Observes the list of skills that should be visible based on prestige level.
     *
     * @return A Flow of filtered skills that updates when skills or prestige changes.
     */
    fun observeVisibleSkills(): Flow<List<Skill>> {
        return skillRepository.observeSkills().combine(
            prestigeRepository.observePrestige()
        ) { skills, prestige ->
            val visibleSkillNames = PrestigeConfig.getVisibleSkills(prestige.level)
            skills.filter { it.name in visibleSkillNames }
        }
    }
}