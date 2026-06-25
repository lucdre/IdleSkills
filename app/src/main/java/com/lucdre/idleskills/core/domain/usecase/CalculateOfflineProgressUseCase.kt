package com.lucdre.idleskills.core.domain.usecase

import com.lucdre.idleskills.core.domain.OfflineProgressManager
import com.lucdre.idleskills.core.domain.OfflineProgressResult
import javax.inject.Inject

/**
 * Calculates offline progress.
 */
class CalculateOfflineProgressUseCase @Inject constructor(
    private val offlineProgressManager: OfflineProgressManager
) {
    suspend operator fun invoke(): OfflineProgressResult? {
        return offlineProgressManager.calculateAndApplyOfflineProgress()
    }
}
