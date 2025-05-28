package com.lucdre.idleskills.prestige.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling prestige-related operations.
 */
interface PrestigeRepositoryInterface {
    /**
     * @return A [Flow] of prestige state that updates when data changes.
     */
    fun observePrestige(): Flow<Prestige>

    /**
     * @return The current prestige state.
     */
    suspend fun getPrestige(): Prestige

    /**
     * Updates the prestige state in the repository.
     *
     * @param prestige The prestige state to be updated.
     */
    suspend fun updatePrestige(prestige: Prestige)
}