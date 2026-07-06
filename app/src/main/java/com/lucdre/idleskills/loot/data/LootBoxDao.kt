package com.lucdre.idleskills.loot.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the loot box entity.
 */
@Dao
interface LootBoxDao {
    /**
     * Observes all loot boxes.
     */
    @Query("SELECT * FROM loot_boxes")
    fun observeLootBoxes(): Flow<List<LootBoxEntity>>

    /**
     * Gets the loot boxes for a specific skill.
     */
    @Query("SELECT * FROM loot_boxes WHERE skillName = :skillName")
    suspend fun getLootBoxBySkill(skillName: String): LootBoxEntity?

    /**
     * Inserts or updates a loot box row.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(lootBox: LootBoxEntity)

    /**
     * Decrements the loot box count, down to a minimum of 0.
     */
    @Query("UPDATE loot_boxes SET count = count - 1 WHERE skillName = :skillName AND count > 0")
    suspend fun decrementLootBoxCount(skillName: String): Int

    /**
     * Updates the loot box count, creating the row if it does not exist.
     */
    @Query("""
        INSERT INTO loot_boxes (skillName, count) 
        VALUES (:skillName, :amount)
        ON CONFLICT(skillName) DO UPDATE SET count = count + excluded.count
    """)
    suspend fun updateLootBoxCount(skillName: String, amount: Int)
}
