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
interface PreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 0")
    fun observePreferences(): Flow<PreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 0")
    suspend fun getPreferences(): PreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: PreferencesEntity)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM player_session WHERE id = 0")
    suspend fun getSession(): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(session: SessionEntity)
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
     * Adds XP, creates row if missing.
     */
    @Query("""
        INSERT INTO skills (name, xp) 
        VALUES (:name, :amount)
        ON CONFLICT(name) DO UPDATE SET xp = MIN(xp + excluded.xp, :cap)
    """)
    suspend fun addXpAtomically(name: String, amount: Int, cap: Int)

    @Query("DELETE FROM skills")
    suspend fun clearSkills()
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

    /**
     * Updates a card's level and bonus.
     */
    @Transaction
    suspend fun upgradeCard(cardType: String, nextLevel: Int, bonus: Float) {
        val existing = getCardByType(cardType) ?: return
        val updated = existing.copy(
            level = nextLevel,
            efficiencyBonus = bonus
        )
        insertOrUpdate(updated)
    }
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
     * Decrements the loot box count.
     */
    @Query("UPDATE loot_boxes SET count = count - 1 WHERE skillName = :skillName AND count > 0")
    suspend fun decrementLootBoxCount(skillName: String): Int

    /**
     * Updates the loot box count, creating row if missing.
     */
    @Query("""
        INSERT INTO loot_boxes (skillName, count) 
        VALUES (:skillName, :amount)
        ON CONFLICT(skillName) DO UPDATE SET count = count + excluded.count
    """)
    suspend fun updateLootBoxCount(skillName: String, amount: Int)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY acquiredAt ASC")
    fun observeItems(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory")
    suspend fun getItems(): List<InventoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: InventoryEntity)

    @Query("UPDATE inventory SET quantity = quantity - :amount WHERE itemId = :itemId AND quantity >= :amount")
    suspend fun decrementQuantity(itemId: Int, amount: Int): Int

    /**
     * Adds an item, creating row if missing.
     * Note: acquiredAt is only set on the initial INSERT to preserve discovery order.
     */
    @Query("""
        INSERT INTO inventory (itemId, quantity, acquiredAt) 
        VALUES (:itemId, :amount, :acquiredAt)
        ON CONFLICT(itemId) DO UPDATE SET quantity = quantity + excluded.quantity
    """)
    suspend fun addItem(itemId: Int, amount: Int, acquiredAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM inventory")
    suspend fun clearInventory()
}
