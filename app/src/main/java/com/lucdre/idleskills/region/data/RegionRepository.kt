package com.lucdre.idleskills.region.data

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.region.domain.RegionRepositoryInterface
import com.lucdre.idleskills.skills.domain.skill.SkillType
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for handling region-specific logic and data.

 * @property trainingMethodRepository The dispatcher used to query available training methods.
 */
@Singleton
class RegionRepository @Inject constructor(
    private val trainingMethodRepository: TrainingMethodRepositoryInterface
) : RegionRepositoryInterface {
    
    /**
     * Retrieves all skills that have at least one training method available in the given region.
     *
     * @param region The region to check for available skills.
     * @return A list of [SkillType]s present in the region.
     */
    override fun getSkillsForRegion(region: Region): List<SkillType> {
        return SkillType.entries.filter { skillType ->
            trainingMethodRepository.getTrainingMethodsForSkill(skillType, region).isNotEmpty()
        }
    }
}
