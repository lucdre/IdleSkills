package com.lucdre.idleskills.profile.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling player statistics operations.
 */
interface StatisticsRepositoryInterface {
    /**
     * Observes the player statistics as a flow.
     *
     * @return A [Flow] of [PlayerStatistics] that updates when data changes.
     */
    fun observeStatistics(): Flow<PlayerStatistics>

    /**
     * Gets the current player statistics snapshot.
     *
     * @return The current [PlayerStatistics].
     */
    suspend fun getStatistics(): PlayerStatistics

    /**
     * Increments the count for a specific training method within a skill.
     *
     * @param skill The skill type.
     * @param methodName The name of the training method.
     * @param amount The amount to increment by, defaults to 1.
     */
    suspend fun incrementCount(skill: com.lucdre.idleskills.skills.domain.skill.SkillType, methodName: String, amount: Int = 1)
}
