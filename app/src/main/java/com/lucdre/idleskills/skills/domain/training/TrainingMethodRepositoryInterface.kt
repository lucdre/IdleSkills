package com.lucdre.idleskills.skills.domain.training

/**
 * Interface for managing training method data.
 */
interface TrainingMethodRepositoryInterface {
    /**
     * Retrieves all training methods available for a specific skill.
     *
     * @param skillName The name of the skill to retrieve training methods for.
     * @return A list of training methods available for the specified skill.
     */
    fun getTrainingMethodsForSkill(skillName: String): List<TrainingMethod>
}