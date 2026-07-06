package com.lucdre.idleskills.skills.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the skill entity.
 */
@Dao
interface SkillDao {
    /**
     * Observes all skills.
     */
    @Query("SELECT * FROM skills")
    fun observeSkills(): Flow<List<SkillEntity>>

    /**
     * Gets all skills.
     */
    @Query("SELECT * FROM skills")
    suspend fun getSkills(): List<SkillEntity>

    /**
     * Gets a skill by its unique name.
     */
    @Query("SELECT * FROM skills WHERE name = :name")
    suspend fun getSkillByName(name: String): SkillEntity?

    /**
     * Inserts or updates a single skill.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(skill: SkillEntity)

    /**
     * Inserts or updates a list of skills.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<SkillEntity>)

    /**
     * Adds XP atomically, creating the skill row if it doesn't exist.
     */
    @Query("""
        INSERT INTO skills (name, xp) 
        VALUES (:name, :amount)
        ON CONFLICT(name) DO UPDATE SET xp = MIN(xp + excluded.xp, :cap)
    """)
    suspend fun addXpAtomically(name: String, amount: Int, cap: Int)

    /**
     * Clears all skill progress.
     */
    @Query("DELETE FROM skills")
    suspend fun clearSkills()
}
