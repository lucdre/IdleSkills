package com.lucdre.idleskills.skills.domain.training

import com.lucdre.idleskills.skills.fishing.data.FishingTrainingMethodRepository
import com.lucdre.idleskills.skills.mining.data.MiningTrainingMethodRepository
import com.lucdre.idleskills.skills.woodcutting.data.WcTrainingMethodRepository
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Dispatches requests for training methods to the appropriate skill-specific repository.
 *
 * This class acts as a central point of access for retrieving [TrainingMethod] lists
 * for various skills. It delegates the actual data retrieval to specific repositories.
 *
 * @property wcRepo The repository responsible for Woodcutting training methods.
 * @property miningRepo The repository responsible for Mining training methods.
 */
@Singleton
class TrainingMethodRepositoryDispatcher @Inject constructor(
    private val wcRepo: WcTrainingMethodRepository,
    private val miningRepo: MiningTrainingMethodRepository,
    private val fishingRepo: FishingTrainingMethodRepository
) : TrainingMethodRepositoryInterface {

    /**
     * Retrieves a list of available [TrainingMethod]s for a given skill.
     *
     * Based on the provided [skillName], this method routes the request to the
     * corresponding skill-specific repository.
     *
     * @param skillName The name of the skill for which to fetch training methods.
     *
     * @return A list of [TrainingMethod] objects available for the specified skill.
     *         Returns an empty list if the [skillName] is not supported.
     */
    override fun getTrainingMethodsForSkill(skillName: String): List<TrainingMethod> = when (skillName) {
        "Woodcutting" -> wcRepo.getTrainingMethodsForSkill(skillName)
        "Mining" -> miningRepo.getTrainingMethodsForSkill(skillName)
        "Fishing" -> fishingRepo.getTrainingMethodsForSkill(skillName)
        // TODO add more as more skills come
        else -> emptyList()
    }
}