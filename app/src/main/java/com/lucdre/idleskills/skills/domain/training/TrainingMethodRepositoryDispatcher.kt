package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Dispatches requests for training methods to the appropriate skill-specific repository.
 *
 * This class acts as a central point of access for retrieving [TrainingMethod] lists
 * for various skills. It delegates the actual data retrieval to specific repositories.
 *
 * @property repositories A map of skill-specific repositories.
 */
@Singleton
class TrainingMethodRepositoryDispatcher @Inject constructor(
    private val repositories: Map<String, @JvmSuppressWildcards Provider<TrainingMethodRepositoryInterface>>
) : TrainingMethodRepositoryInterface {

    /**
     * Retrieves a list of available [TrainingMethod]s for a given skill in a specific region.
     *
     * Based on the provided [skill], this method routes the request to the
     * corresponding skill-specific repository.
     *
     * @param skill The skill for which to fetch training methods.
     * @param region The region for which to fetch training methods.
     *
     * @return A list of [TrainingMethod] objects available for the specified skill and region.
     *         Returns an empty list if the skill is not supported.
     */
    override fun getTrainingMethodsForSkill(skill: SkillType, region: Region): List<TrainingMethod> {
        return repositories[skill.name]?.get()?.getTrainingMethodsForSkill(skill, region) ?: emptyList()
    }
}
