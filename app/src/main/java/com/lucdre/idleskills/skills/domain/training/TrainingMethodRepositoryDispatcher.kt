package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.skill.SkillType
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Routes training method requests to the right skill repository.
 *
 * @property repositories A map of skill-specific repositories.
 */
@Singleton
class TrainingMethodRepositoryDispatcher @Inject constructor(
    private val repositories: Map<String, @JvmSuppressWildcards Provider<TrainingMethodRepositoryInterface>>
) : TrainingMethodRepositoryInterface {

    /**
     * Fetch available methods for [skill] in [region]
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
