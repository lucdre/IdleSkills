package com.lucdre.idleskills.profile.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the profile entity.
 */
@Dao
interface ProfileDao {
    /**
     * Observes the single player profile row.
     */
    @Query("SELECT * FROM player_profile WHERE id = 0")
    fun observeProfile(): Flow<ProfileEntity?>

    /**
     * Gets the single player profile.
     */
    @Query("SELECT * FROM player_profile WHERE id = 0")
    suspend fun getProfile(): ProfileEntity?

    /**
     * Inserts or updates the player profile.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: ProfileEntity)
}
