package com.lucdre.idleskills.profile.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the user preferences entity.
 */
@Dao
interface PreferencesDao {
    /**
     * Observes the single user preferences row.
     */
    @Query("SELECT * FROM user_preferences WHERE id = 0")
    fun observePreferences(): Flow<PreferencesEntity?>

    /**
     * Gets the single user preferences.
     */
    @Query("SELECT * FROM user_preferences WHERE id = 0")
    suspend fun getPreferences(): PreferencesEntity?

    /**
     * Inserts or updates the user preferences.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: PreferencesEntity)
}
