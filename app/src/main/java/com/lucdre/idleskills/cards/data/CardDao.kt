package com.lucdre.idleskills.cards.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the card entity.
 */
@Dao
interface CardDao {
    /**
     * Observes all cards in the collection.
     */
    @Query("SELECT * FROM cards")
    fun observeAllCards(): Flow<List<CardEntity>>

    /**
     * Gets a card by its unique type name.
     */
    @Query("SELECT * FROM cards WHERE cardType = :type")
    suspend fun getCardByType(type: String): CardEntity?

    /**
     * Gets cards matching any of the specified type names.
     */
    @Query("SELECT * FROM cards WHERE cardType IN (:types)")
    suspend fun getCardsByTypes(types: List<String>): List<CardEntity>

    /**
     * Gets the total count of cards initialized in the database.
     */
    @Query("SELECT COUNT(*) FROM cards")
    suspend fun getCount(): Int

    /**
     * Inserts or updates a single card.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(card: CardEntity)

    /**
     * Inserts or updates multiple cards.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<CardEntity>)

    /**
     * Upgrades a card's level and efficiency bonus.
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
