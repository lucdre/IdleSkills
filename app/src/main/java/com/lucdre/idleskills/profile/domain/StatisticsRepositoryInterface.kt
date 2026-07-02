package com.lucdre.idleskills.profile.domain

import com.lucdre.idleskills.skills.domain.skill.SkillType
import kotlinx.coroutines.flow.Flow

/**
 * Player statistics repository interface.
 */
interface StatisticsRepositoryInterface {
    /**
     * Observe stats.
     */
    fun observeStatistics(): Flow<PlayerStatistics>

    /**
     * Get stats snapshot.
     */
    suspend fun getStatistics(): PlayerStatistics

    /**
     * Increments the count for a specific training method within a skill.
     *
     * @param skill The skill type.
     * @param methodName The name of the training method.
     * @param amount The amount to increment by, defaults to 1.
     */
    suspend fun incrementCount(skill: SkillType, methodName: String, amount: Int = 1)
}
