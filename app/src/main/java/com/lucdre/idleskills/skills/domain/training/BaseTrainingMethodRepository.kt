package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Base filtering for training methods by region.
 */
abstract class BaseTrainingMethodRepository : TrainingMethodRepositoryInterface {

    /**
     * Map of training methods by skill.
     */
    protected abstract val trainingMethods: Map<SkillType, List<TrainingMethod>>

    /**
     * Filters methods by [region].
     *
     * @param skill The skill to get training methods for
     * @param region The region for which to fetch training methods
     * @return List of training methods available for the skill and region, or empty list if not found
     */
    override fun getTrainingMethodsForSkill(skill: SkillType, region: Region): List<TrainingMethod> {
        return (trainingMethods[skill] ?: emptyList()).filter {
            it.availableRegions.isEmpty() || region in it.availableRegions 
        }
    }
}
