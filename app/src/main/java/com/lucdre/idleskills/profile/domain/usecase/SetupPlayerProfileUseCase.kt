package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import javax.inject.Inject

/**
 * Use case for setting up a new player profile.
 */
class SetupPlayerProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepositoryInterface
) {
    /**
     * Initializes the player profile with the provided username.
     * 
     * @param username The player's name.
     * @return True if setup was successful.
     */
    suspend operator fun invoke(username: String): Boolean {
        if (username.isBlank()) return false
        
        val profile = PlayerProfile(
            username = username
        )
        
        profileRepository.updateProfile(profile)
        return true
    }
}
