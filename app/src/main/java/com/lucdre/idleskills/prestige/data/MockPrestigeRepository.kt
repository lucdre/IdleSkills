package com.lucdre.idleskills.prestige.data

import com.lucdre.idleskills.prestige.domain.Prestige
import com.lucdre.idleskills.prestige.domain.PrestigeRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock prestige repository, will be replaced by something better in the future.
 */
@Singleton
class MockPrestigeRepository @Inject constructor() : PrestigeRepositoryInterface {

    private val _prestige = MutableStateFlow(Prestige())

    override fun observePrestige(): Flow<Prestige> {
        return _prestige.asStateFlow()
    }

    override suspend fun getPrestige(): Prestige {
        return _prestige.value
    }

    override suspend fun updatePrestige(prestige: Prestige) {
        _prestige.value = prestige
    }
}