package com.lucdre.idleskills.core.persistence

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

/**
 * Data access object for player session operations.
 */
@Dao
interface SessionDao {
    /**
     * Gets the current session state.
     */
    @Query("SELECT * FROM player_session WHERE id = 0")
    suspend fun getSession(): SessionEntity?

    /**
     * Inserts or updates the player session state.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(session: SessionEntity)
}
