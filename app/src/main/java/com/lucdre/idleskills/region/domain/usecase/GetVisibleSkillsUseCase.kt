package com.lucdre.idleskills.region.domain.usecase

import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import com.lucdre.idleskills.region.domain.RegionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.Skill
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for retrieving skills that should be visible to the player.
 *
 * Visible skills are determined by the current region the player is in.
 *
 * @property skillRepository The repository for skill data.
 * @property sessionRepository The repository for session data.
 * @property regionRepository The repository for region data.
 */
class GetVisibleSkillsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val sessionRepository: SessionRepositoryInterface,
    private val regionRepository: RegionRepositoryInterface
) {
    /**
     * Returns the list of skills that should be visible based on the current region.
     *
     * @return A filtered list of skills available in the region.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val currentRegion = sessionRepository.getCurrentRegion()
        val regionSkills = regionRepository.getSkillsForRegion(currentRegion)

        val skillMap = skills.associateBy { it.type }
        
        return regionSkills.mapNotNull { type ->
            skillMap[type]
        }
    }

    /**
     * Observes the list of skills that should be visible based on current region.
     *
     * @return A Flow of filtered skills that updates when skills or player region changes.
     */
    fun observeVisibleSkills(): Flow<List<Skill>> {
        return combine(
            skillRepository.observeSkills(),
            sessionRepository.observeCurrentRegion()
        ) { skills, currentRegion ->
            val regionSkills = regionRepository.getSkillsForRegion(currentRegion)
            
            val skillMap = skills.associateBy { it.type }
            
            regionSkills.mapNotNull { type ->
                skillMap[type]
            }
        }
    }
}
