package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import javax.inject.Inject

/**
 * Use case for setting up the player profile at the start of the game.
 *
 * @property profileRepository The repository for player profile data.
 */
class SetupPlayerProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepositoryInterface,
) {
    /**
     * Sets up the player profile with a username and favorite skill.
     *
     * @param username The player's chosen username.
     * @param favoriteSkill The player's favorite skill.
     * @return True if setup was successful.
     */
    suspend operator fun invoke(username: String, favoriteSkill: String): Boolean {
        if (username.isBlank() || favoriteSkill.isBlank()) return false

        // Update Profile
        val currentProfile = profileRepository.getProfile()
        val updatedProfile = currentProfile.copy(
            username = username,
            favoriteSkill = favoriteSkill
        )
        profileRepository.updateProfile(updatedProfile)

        return true
    }
}
