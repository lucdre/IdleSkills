package com.lucdre.idleskills.profile.data

import com.lucdre.idleskills.core.persistence.ProfileDao
import com.lucdre.idleskills.core.persistence.ProfileEntity
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for player profile data.
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepositoryInterface {

    override fun observeProfile(): Flow<PlayerProfile> {
        return profileDao.observeProfile().map { entity ->
            entity?.toDomain() ?: PlayerProfile()
        }
    }

    override suspend fun getProfile(): PlayerProfile {
        return profileDao.getProfile()?.toDomain() ?: PlayerProfile()
    }

    override suspend fun updateProfile(profile: PlayerProfile) {
        profileDao.insertOrUpdate(profile.toEntity())
    }

    private fun ProfileEntity.toDomain(): PlayerProfile {
        return PlayerProfile(
            playerId = playerId,
            username = username,
            hasCompletedSetup = hasCompletedSetup
        )
    }

    private fun PlayerProfile.toEntity(): ProfileEntity {
        return ProfileEntity(
            id = 0,
            playerId = playerId,
            username = username,
            hasCompletedSetup = hasCompletedSetup
        )
    }
}
