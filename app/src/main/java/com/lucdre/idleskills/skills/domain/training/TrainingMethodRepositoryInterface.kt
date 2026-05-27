package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType

/**
 * Interface for managing training method data.
 */
interface TrainingMethodRepositoryInterface {
    /**
     * Retrieves all training methods available for a specific skill in a given region.
     *
     * @param skill The skill to retrieve training methods for.
     * @param region The region for which to fetch training methods.
     * @return A list of training methods available for the specified skill in the given region.
     */
    fun getTrainingMethodsForSkill(skill: SkillType, region: Region): List<TrainingMethod>
}
