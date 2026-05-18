package com.lucdre.idleskills.profile.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling player profile operations.
 */
interface ProfileRepositoryInterface {
    /**
     * @return A [Flow] of the player profile that updates when data changes.
     */
    fun observeProfile(): Flow<PlayerProfile>

    /**
     * @return The current player profile snapshot.
     */
    suspend fun getProfile(): PlayerProfile

    /**
     * Updates the player profile in the repository.
     *
     * @param profile The profile to be updated.
     */
    suspend fun updateProfile(profile: PlayerProfile)
}
