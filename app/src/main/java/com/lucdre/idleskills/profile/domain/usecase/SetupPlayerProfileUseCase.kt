package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import java.util.UUID
import javax.inject.Inject

/**
 * Initial profile setup.
 */
class SetupPlayerProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepositoryInterface
) {
    /**
     * Initializes the player profile with the provided username and a generated player ID.
     * 
     * @param username The player's name.
     * @return True if setup was successful.
     */
    suspend operator fun invoke(username: String): Result<Unit> {
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("Username cannot be empty."))
        }
        
        if (username.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters long."))
        }
        
        val profile = PlayerProfile(
            playerId = UUID.randomUUID().toString(),
            username = username.trim(),
            hasCompletedSetup = true
        )
        
        return try {
            profileRepository.updateProfile(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
