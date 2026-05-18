package com.lucdre.idleskills.region.domain.usecase

import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.region.domain.RegionConfig
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
 * @property profileRepository The repository for profile data.
 */
class GetVisibleSkillsUseCase @Inject constructor(
    private val skillRepository: SkillRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface
) {
    /**
     * Returns the list of skills that should be visible based on the current region.
     *
     * @return A filtered list of skills available in the region.
     */
    suspend operator fun invoke(): List<Skill> {
        val skills = skillRepository.getSkills()
        val profile = profileRepository.getProfile()
        
        val currentRegion = profile.currentRegion
        val regionSkillNames = RegionConfig.getSkillsForRegion(currentRegion)
        
        return skills.filter { it.name in regionSkillNames }
    }

    /**
     * Observes the list of skills that should be visible based on current region.
     *
     * @return A Flow of filtered skills that updates when skills or player region changes.
     */
    fun observeVisibleSkills(): Flow<List<Skill>> {
        return combine(
            skillRepository.observeSkills(),
            profileRepository.observeProfile()
        ) { skills, profile ->
            val currentRegion = profile.currentRegion
            val regionSkillNames = RegionConfig.getSkillsForRegion(currentRegion)
            
            skills.filter { it.name in regionSkillNames }
        }
    }
}
