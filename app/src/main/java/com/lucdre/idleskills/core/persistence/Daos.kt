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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(skill: SkillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<SkillEntity>)

    @Query("DELETE FROM skills")
    suspend fun clearSkills()
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLootBox(lootBox: LootBoxEntity)
}
