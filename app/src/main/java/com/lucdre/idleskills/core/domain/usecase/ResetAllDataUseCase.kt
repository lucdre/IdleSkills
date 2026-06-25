package com.lucdre.idleskills.core.domain.usecase

import com.lucdre.idleskills.core.persistence.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Wipes all game data.
 */
class ResetAllDataUseCase @Inject constructor(
    private val database: AppDatabase
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        database.clearAllTables()
    }
}
