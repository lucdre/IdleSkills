package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import com.lucdre.idleskills.profile.domain.PlayerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for observing and retrieving the player's profile.
 *
 * @property prestigeRepository The repository where the profile is currently stored.
 */
class GetPlayerProfileUseCase @Inject constructor(
    private val prestigeRepository: PrestigeRepositoryInterface
) {
    /**
     * Observes the player profile as a flow.
     */
    fun observeProfile(): Flow<PlayerProfile> {
        return prestigeRepository.observePrestige().map { it.playerProfile }
    }

    /**
     * Gets the current player profile snapshot.
     */
    suspend operator fun invoke(): PlayerProfile {
        return prestigeRepository.getPrestige().playerProfile
    }
}
