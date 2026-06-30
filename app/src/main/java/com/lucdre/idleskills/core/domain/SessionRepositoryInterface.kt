package com.lucdre.idleskills.core.domain

import com.lucdre.idleskills.region.domain.Region
import com.lucdre.idleskills.skills.domain.training.ActiveTraining
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing player sessions and cross-domain game state (like active training).
 */
interface SessionRepositoryInterface {
    /**
     * Gets the full session metadata (active training, timestamp, region).
     */
    suspend fun getSessionData(): SessionData

    /**
     * Observes the current player region.
     */
    fun observeCurrentRegion(): Flow<Region>

    /**
     * Gets the current player region.
     */
    suspend fun getCurrentRegion(): Region

    /**
     * Sets the current player region.
     */
    suspend fun setCurrentRegion(region: Region)
    /**
     * Observes the current active training session.
     */
    fun observeActiveTraining(): Flow<ActiveTraining?>

    /**
     * Sets the current active training session.
     */
    suspend fun setActiveTraining(training: ActiveTraining?)

    /**
     * Updates the last saved timestamp in the current session.
     */
    suspend fun updateLastSavedTimestamp()

    /**
     * Saves any pending in-memory changes to the database.
     */
    suspend fun syncToPersistence()
}
