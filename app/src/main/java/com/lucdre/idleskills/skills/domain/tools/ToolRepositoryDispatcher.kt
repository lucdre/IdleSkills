package com.lucdre.idleskills.skills.domain.tools

import com.lucdre.idleskills.skills.mining.data.MiningToolRepository
import com.lucdre.idleskills.skills.woodcutting.data.WcToolRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dispatches requests for tools to the appropriate skill-specific repository.
 *
 * This class acts as a central point of access for retrieving [Tool] lists
 * for various skills. It delegates the actual data retrieval to specific repositories.
 *
 * @property wcRepo The repository responsible for Woodcutting tools.
 * @property miningRepo The repository responsible for Mining tools.
 */
@Singleton
class ToolRepositoryDispatcher @Inject constructor(
    private val wcRepo: WcToolRepository,
    private val miningRepo: MiningToolRepository
) : ToolRepositoryInterface {

    /**
     * Retrieves a list of available [Tool]s for a given skill.
     *
     * Based on the provided [skillName], this method routes the request to the
     * corresponding skill-specific repository.
     *
     * @param skillName The name of the skill for which to fetch tools.
     *
     * @return A list of [Tool] objects available for the specified skill.
     *         Returns an empty list if the [skillName] is not supported.
     */
    override fun getToolsForSkill(skillName: String): List<Tool> = when (skillName) {
        "Woodcutting" -> wcRepo.getToolsForSkill(skillName)
        "Mining" -> miningRepo.getToolsForSkill(skillName)
        // TODO add more as needed
        else -> emptyList()
    }
}