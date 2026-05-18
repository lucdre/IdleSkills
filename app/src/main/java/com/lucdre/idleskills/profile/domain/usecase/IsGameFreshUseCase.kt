package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import javax.inject.Inject

/**
 * Use case for checking if this is a fresh game (no initial skill selected yet).
 *
 * @property profileRepository The repository for player profile data.
 */
class IsGameFreshUseCase @Inject constructor(
    private val profileRepository: ProfileRepositoryInterface,
) {
    /**
     * Checks if the game is fresh (no initial skill has been selected).
     *
     * @return True if no initial skill is selected, false otherwise.
     */
    suspend operator fun invoke(): Boolean {
        val profile = profileRepository.getProfile()
        return profile.username.isEmpty()
    }
}
