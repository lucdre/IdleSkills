package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import javax.inject.Inject

/**
 * Returns the current prestige state with updated requirement status.
 *
 * Gets the stored prestige level and checks if requirements are currently met to prestige.
 *
 * @property prestigeRepository The repository for prestige data.
 * @property checkPrestigeRequirementsUseCase Use case to verify current prestige requirements.
 *
 * @return A [Prestige] object with current level and whether prestiging is possible.
 */
class GetPrestigeStateUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
    private val checkPrestigeRequirementsUseCase: CheckPrestigeRequirementsUseCase
) {
    suspend operator fun invoke(): Prestige {
        val currentPrestige = prestigeRepository.getPrestige()
        val canPrestige = checkPrestigeRequirementsUseCase()

        return currentPrestige.copy(canPrestige = canPrestige)
    }
}
