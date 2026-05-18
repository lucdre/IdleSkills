package com.lucdre.idleskills.profile.data

import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing the player profile.
 */
@Singleton
class ProfileRepository @Inject constructor() : ProfileRepositoryInterface {

    private val _profile = MutableStateFlow(PlayerProfile())

    override fun observeProfile(): Flow<PlayerProfile> {
        return _profile.asStateFlow()
    }

    override suspend fun getProfile(): PlayerProfile {
        return _profile.value
    }

    override suspend fun updateProfile(profile: PlayerProfile) {
        _profile.value = profile
    }
}
