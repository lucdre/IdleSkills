package com.lucdre.idleskills.core.persistence

import androidx.room3.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM player_profile WHERE id = 0")
    fun observeProfile(): Flow<ProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 0")
    suspend fun getProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: ProfileEntity)
}

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills")
    fun observeSkills(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills")
    suspend fun getSkills(): List<SkillEntity>

    @Query("SELECT * FROM skills WHERE name = :name")
    suspend fun getSkillByName(name: String): SkillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(skill: SkillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<SkillEntity>)

    /**
     * Atomically adds XP to a skill.
     */
    @Query("UPDATE skills SET xp = MIN(xp + :amount, 200000000) WHERE name = :name")
    suspend fun addXpAtomically(name: String, amount: Int)

    @Query("DELETE FROM skills")
    suspend fun clearSkills()
}

/**
 * Specialized DAO for operations involving multiple tables that must be atomic.
 */
@Dao
interface OfflineProgressDao {
    @Query("UPDATE skills SET xp = xp + :amount WHERE name = :name")
    suspend fun addXpAtomically(name: String, amount: Int)

    @Query("SELECT * FROM player_profile WHERE id = 0")
    suspend fun getProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfile(profile: ProfileEntity)

    /**
     * Atomic operation to apply offline XP and update the save timestamp.
     */
    @Transaction
    suspend fun applyOfflineProgress(skillName: String, amount: Int, now: Long) {
        addXpAtomically(skillName, amount)
        val profile = getProfile()
        if (profile != null) {
            updateProfile(profile.copy(lastSavedTimestamp = now))
        }
    }
}

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun observeAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE cardType = :type")
    suspend fun getCardByType(type: String): CardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(card: CardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<CardEntity>)
}

@Dao
interface LootBoxDao {
    @Query("SELECT * FROM loot_boxes")
    fun observeLootBoxes(): Flow<List<LootBoxEntity>>

    @Query("SELECT * FROM loot_boxes WHERE skillName = :skillName")
    suspend fun getLootBoxBySkill(skillName: String): LootBoxEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(lootBox: LootBoxEntity)

    /**
     * Atomically updates the loot box count.
     */
    @Query("UPDATE loot_boxes SET count = count + :amount WHERE skillName = :skillName")
    suspend fun updateLootBoxCount(skillName: String, amount: Int)
}
