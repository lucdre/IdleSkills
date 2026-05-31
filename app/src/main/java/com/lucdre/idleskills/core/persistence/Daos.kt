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
interface SessionDao {
    @Query("SELECT * FROM player_session WHERE id = 0")
    fun observeSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM player_session WHERE id = 0")
    suspend fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(session: SessionEntity)
}

@Dao
interface PrestigeDao {
    @Query("SELECT * FROM prestige_state WHERE id = 0")
    fun observePrestige(): Flow<PrestigeEntity?>

    @Query("SELECT * FROM prestige_state WHERE id = 0")
    suspend fun getPrestige(): PrestigeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(prestige: PrestigeEntity)
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
     * Internal helper to increment XP if row exists.
     */
    @Query("UPDATE skills SET xp = MIN(xp + :amount, :cap) WHERE name = :name")
    suspend fun incrementXp(name: String, amount: Int, cap: Int): Int

    /**
     * Atomically adds XP to a skill, creating the row if it doesn't exist.
     */
    @Transaction
    suspend fun addXpAtomically(name: String, amount: Int, cap: Int) {
        val affected = incrementXp(name, amount, cap)
        if (affected == 0) {
            insertOrUpdate(SkillEntity(name, amount))
        }
    }

    @Query("DELETE FROM skills")
    suspend fun clearSkills()
}

/**
 * Specialized DAO for operations involving multiple tables that must be atomic.
 */
@Dao
interface OfflineProgressDao {
    @Query("UPDATE skills SET xp = MIN(xp + :amount, :cap) WHERE name = :name")
    suspend fun incrementXp(name: String, amount: Int, cap: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSkill(skill: SkillEntity)

    @Query("SELECT * FROM player_session WHERE id = 0")
    suspend fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSession(session: SessionEntity)

    /**
     * Atomic operation to apply offline XP and update the save timestamp.
     */
    @Transaction
    suspend fun applyOfflineProgress(skillName: String, amount: Int, now: Long, cap: Int) {
        val affected = incrementXp(skillName, amount, cap)
        if (affected == 0) {
            insertOrUpdateSkill(SkillEntity(skillName, amount))
        }
        val session = getSession() ?: SessionEntity()
        updateSession(session.copy(lastSavedTimestamp = now))
    }
}

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun observeAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE cardType = :type")
    suspend fun getCardByType(type: String): CardEntity?

    @Query("SELECT * FROM cards WHERE cardType IN (:types)")
    suspend fun getCardsByTypes(types: List<String>): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun getCount(): Int

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
     * Internal helper to update the loot box count.
     */
    @Query("UPDATE loot_boxes SET count = count + :amount WHERE skillName = :skillName")
    suspend fun incrementLootBoxCount(skillName: String, amount: Int): Int

    /**
     * Atomically updates the loot box count, creating the row if it doesn't exist.
     */
    @Transaction
    suspend fun updateLootBoxCount(skillName: String, amount: Int) {
        val affected = incrementLootBoxCount(skillName, amount)
        if (affected == 0) {
            insertOrUpdate(LootBoxEntity(skillName, amount))
        }
    }
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory")
    fun observeItems(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory")
    suspend fun getItems(): List<InventoryEntity>

    @Query("SELECT * FROM inventory WHERE itemId = :itemId")
    suspend fun getItemById(itemId: Int): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: InventoryEntity)

    @Query("UPDATE inventory SET quantity = quantity + :amount WHERE itemId = :itemId")
    suspend fun incrementQuantity(itemId: Int, amount: Int)

    @Transaction
    suspend fun addItem(itemId: Int, amount: Int) {
        val existing = getItemById(itemId)
        if (existing != null) {
            incrementQuantity(itemId, amount)
        } else {
            insertOrUpdate(InventoryEntity(itemId, amount))
        }
    }

    @Transaction
    suspend fun addItems(items: List<InventoryEntity>) {
        items.forEach { item ->
            val existing = getItemById(item.itemId)
            if (existing != null) {
                incrementQuantity(item.itemId, item.quantity)
            } else {
                insertOrUpdate(item)
            }
        }
    }

    @Query("DELETE FROM inventory")
    suspend fun clearInventory()
}
