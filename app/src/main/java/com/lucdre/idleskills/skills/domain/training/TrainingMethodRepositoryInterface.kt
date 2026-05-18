package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.region.domain.Region

/**
 * Interface for managing training method data.
 */
interface TrainingMethodRepositoryInterface {
    /**
     * Retrieves all training methods available for a specific skill in a given region.
     *
     * @param skillName The name of the skill to retrieve training methods for.
     * @param region The region for which to fetch training methods.
     * @return A list of training methods available for the specified skill in the given region.
     */
    fun getTrainingMethodsForSkill(skillName: String, region: Region): List<TrainingMethod>
}
