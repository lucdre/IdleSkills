package com.lucdre.idleskills.profile.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing user-specific settings and application preferences.
 */
interface PreferencesRepositoryInterface {
    /**
     * Observes the user preferences as a flow.
     */
    fun observePreferences(): Flow<UserPreferences>

    /**
     * Returns a snapshot of the current user preferences.
     */
    suspend fun getPreferences(): UserPreferences

    /**
     * Updates the user preferences.
     */
    suspend fun updatePreferences(preferences: UserPreferences)
}
