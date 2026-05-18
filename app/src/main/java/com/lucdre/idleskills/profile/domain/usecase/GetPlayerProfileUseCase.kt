package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing and retrieving the player's profile.
 *
 * @property profileRepository The repository where the profile is stored.
 */
class GetPlayerProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepositoryInterface
) {
    /**
     * Observes the player profile as a flow.
     */
    fun observeProfile(): Flow<PlayerProfile> {
        return profileRepository.observeProfile()
    }

    /**
     * Gets the current player profile snapshot.
     */
    suspend operator fun invoke(): PlayerProfile {
        return profileRepository.getProfile()
    }
}
