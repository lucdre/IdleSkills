package com.lucdre.idleskills.prestige.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import javax.inject.Inject

/**
 * Use case for checking if this is a fresh game (no initial skill selected yet).
 *
 * @property prestigeRepository The repository for prestige data.
 */
class IsGameFreshUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface,
) {
    /**
     * Checks if the game is fresh (no initial skill has been selected).
     *
     * @return True if no initial skill is selected, false otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val prestige = prestigeRepository.getPrestige()
        return prestige.skillTreeProgress.selectedInitialSkill == null
    }
}
