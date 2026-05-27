package com.lucdre.idleskills.core.persistence

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters

/**
 * Main database for the application.
 */
@Database(
    entities = [
        ProfileEntity::class,
        SkillEntity::class,
        CardEntity::class,
        LootBoxEntity::class
    ],
    version = 1,
    exportSchema = true // Enabled to support auto-migrations in the future
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun skillDao(): SkillDao
    abstract fun cardDao(): CardDao
    abstract fun lootBoxDao(): LootBoxDao
    abstract fun offlineProgressDao(): OfflineProgressDao

    /**
     * Resets the entire database by clearing all tables.
     */
    suspend fun resetAllData() {
        clearAllTables()
    }
}
