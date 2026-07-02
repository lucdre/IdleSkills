package com.lucdre.idleskills.core.persistence

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.ColumnTypeConverters

/**
 * Main database for the application.
 */
@Database(
    entities = [
        ProfileEntity::class,
        PreferencesEntity::class,
        SessionEntity::class,
        PrestigeEntity::class,
        SkillEntity::class,
        CardEntity::class,
        LootBoxEntity::class,
        InventoryEntity::class,
    ],
    version = 1,
    exportSchema = true // Enabled to support auto-migrations in the future
)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun sessionDao(): SessionDao
    abstract fun prestigeDao(): PrestigeDao
    abstract fun skillDao(): SkillDao
    abstract fun cardDao(): CardDao
    abstract fun lootBoxDao(): LootBoxDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun progressApplicationDao(): ProgressApplicationDao
}
