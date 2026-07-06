package com.lucdre.idleskills.profile.data

import com.lucdre.idleskills.profile.domain.PreferencesRepositoryInterface
import com.lucdre.idleskills.profile.domain.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user preferences persisted in the local database.
 *
 * @property preferencesDao The Data Access Object for preferences.
 */
@Singleton
class PreferencesRepository @Inject constructor(
    private val preferencesDao: PreferencesDao
) : PreferencesRepositoryInterface {

    override fun observePreferences(): Flow<UserPreferences> {
        return preferencesDao.observePreferences().map { entity ->
            entity?.toDomain() ?: UserPreferences()
        }
    }

    override suspend fun getPreferences(): UserPreferences {
        return preferencesDao.getPreferences()?.toDomain() ?: UserPreferences()
    }

    override suspend fun updatePreferences(preferences: UserPreferences) {
        preferencesDao.insertOrUpdate(preferences.toEntity())
    }

    private fun PreferencesEntity.toDomain(): UserPreferences {
        return UserPreferences(
            isNotificationsEnabled = isNotificationsEnabled,
            theme = theme
        )
    }

    private fun UserPreferences.toEntity(): PreferencesEntity {
        return PreferencesEntity(
            id = 0,
            isNotificationsEnabled = isNotificationsEnabled,
            theme = theme
        )
    }
}
