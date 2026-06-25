package com.lucdre.idleskills.profile.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling player profile operations.
 */
interface ProfileRepositoryInterface {
    /**
     * Observe profile.
     */
    fun observeProfile(): Flow<PlayerProfile>

    /**
     * @return The current player profile snapshot.
     */
    suspend fun getProfile(): PlayerProfile

    /**
     * Update profile.
     */
    suspend fun updateProfile(profile: PlayerProfile)
}
