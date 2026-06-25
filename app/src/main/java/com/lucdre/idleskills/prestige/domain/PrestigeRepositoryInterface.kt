package com.lucdre.idleskills.prestige.domain

import kotlinx.coroutines.flow.Flow

/**
 * Prestige repository interface.
 */
interface PrestigeRepositoryInterface {
    /**
     * Observe prestige state.
     */
    fun observePrestige(): Flow<Prestige>

    /**
     * @return The current prestige state.
     */
    suspend fun getPrestige(): Prestige

    /**
     * Update prestige state.
     */
    suspend fun updatePrestige(prestige: Prestige)
}
