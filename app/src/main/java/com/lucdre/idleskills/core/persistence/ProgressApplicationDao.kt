package com.lucdre.idleskills.core.persistence

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.lucdre.idleskills.inventory.data.InventoryEntity
import com.lucdre.idleskills.skills.data.SkillEntity

/**
 * Bulk updates for offline progress.
 */
@Dao
interface ProgressApplicationDao {

    @Query("UPDATE skills SET xp = MIN(xp + :amount, :cap) WHERE name = :name")
    suspend fun incrementXp(name: String, amount: Int, cap: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSkill(skill: SkillEntity)

    @Query("SELECT * FROM inventory WHERE itemId = :itemId")
    suspend fun getInventoryItemById(itemId: Int): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateInventoryItem(item: InventoryEntity)

    @Query("UPDATE inventory SET quantity = quantity + :amount WHERE itemId = :itemId")
    suspend fun incrementInventoryQuantity(itemId: Int, amount: Int)

    @Query("UPDATE player_session SET lastSavedTimestamp = :now WHERE id = 0")
    suspend fun updateSessionTimestamp(now: Long)

    /**
     * Applies offline rewards and updates the save timestamp.
     */
    @Transaction
    suspend fun applyOfflineProgress(
        skillName: String,
        xpAmount: Int,
        items: List<InventoryEntity>,
        now: Long,
        xpCap: Int
    ) {
        // Apply XP
        if (xpAmount > 0) {
            val affected = incrementXp(skillName, xpAmount, xpCap)
            if (affected == 0) {
                insertOrUpdateSkill(SkillEntity(skillName, xpAmount))
            }
        }

        // Apply Items
        items.forEach { item ->
            val existing = getInventoryItemById(item.itemId)
            if (existing != null) {
                incrementInventoryQuantity(item.itemId, item.quantity)
            } else {
                insertOrUpdateInventoryItem(item)
            }
        }

        // Update Session Timestamp
        updateSessionTimestamp(now)
    }
}
