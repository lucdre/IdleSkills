package com.lucdre.idleskills.profile.data

import com.lucdre.idleskills.core.persistence.ProfileDao
import com.lucdre.idleskills.core.persistence.ProfileEntity
import com.lucdre.idleskills.core.persistence.SessionDao
import com.lucdre.idleskills.core.persistence.SessionEntity
import com.lucdre.idleskills.profile.domain.PlayerProfile
import com.lucdre.idleskills.profile.domain.ProfileRepositoryInterface
import com.lucdre.idleskills.region.domain.Region
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for managing the player profile.
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val sessionDao: SessionDao,
) : ProfileRepositoryInterface {

    override fun observeProfile(): Flow<PlayerProfile> {
        return profileDao.observeProfile().combine(sessionDao.observeSession()) { profile, session ->
            PlayerProfile(
                username = profile?.username ?: "",
                currentRegion = session?.currentRegion ?: Region.FIRST_REGION
            )
        }
    }

    override suspend fun getProfile(): PlayerProfile {
        val profile = profileDao.getProfile()
        val session = sessionDao.getSession()
        return PlayerProfile(
            username = profile?.username ?: "",
            currentRegion = session?.currentRegion ?: Region.FIRST_REGION
        )
    }

    override suspend fun updateProfile(profile: PlayerProfile) {
        val currentProfile = profileDao.getProfile() ?: ProfileEntity()
        profileDao.insertOrUpdate(currentProfile.copy(username = profile.username))
        
        val currentSession = sessionDao.getSession() ?: SessionEntity()
        sessionDao.insertOrUpdate(
            currentSession.copy(
                currentRegion = profile.currentRegion,
                lastSavedTimestamp = System.currentTimeMillis()
            )
        )
    }
}
