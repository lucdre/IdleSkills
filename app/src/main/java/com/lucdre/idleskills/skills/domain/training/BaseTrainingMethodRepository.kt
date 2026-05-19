package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.region.domain.Region

/**
 * Base repository implementation for training methods.
 * 
 * Provides common filtering logic for training methods based on the current region.
 */
abstract class BaseTrainingMethodRepository : TrainingMethodRepositoryInterface {

    /**
     * Map of training methods, where the key is the skill name.
     */
    protected abstract val trainingMethods: Map<String, List<TrainingMethod>>

    /**
     * Retrieves training methods available for the specified skill in a given region.
     *
     * @param skillName The name of the skill to get training methods for
     * @param region The region for which to fetch training methods
     * @return List of training methods available for the skill and region, or empty list if not found
     */
    override fun getTrainingMethodsForSkill(skillName: String, region: Region): List<TrainingMethod> {
        return (trainingMethods[skillName] ?: emptyList()).filter { 
            it.availableRegions.isEmpty() || region in it.availableRegions 
        }
    }
}
