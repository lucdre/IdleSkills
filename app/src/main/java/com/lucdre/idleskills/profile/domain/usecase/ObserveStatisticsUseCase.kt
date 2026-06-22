package com.lucdre.idleskills.profile.domain.usecase

import com.lucdre.idleskills.profile.domain.PlayerStatistics
import com.lucdre.idleskills.profile.domain.StatisticsRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing and retrieving the player's statistics.
 *
 * @property statisticsRepository The repository where the statistics are stored.
 */
class ObserveStatisticsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepositoryInterface
) {
    /**
     * Observes the player statistics as a flow.
     */
    fun observeStatistics(): Flow<PlayerStatistics> {
        return statisticsRepository.observeStatistics()
    }

}
