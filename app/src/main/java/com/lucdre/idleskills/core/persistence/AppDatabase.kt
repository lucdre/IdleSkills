package com.lucdre.idleskills.core.persistence

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.lucdre.idleskills.cards.data.CardDao
import com.lucdre.idleskills.cards.data.CardEntity
import com.lucdre.idleskills.inventory.data.InventoryDao
import com.lucdre.idleskills.inventory.data.InventoryEntity
import com.lucdre.idleskills.loot.data.LootBoxDao
import com.lucdre.idleskills.loot.data.LootBoxEntity
import com.lucdre.idleskills.profile.data.PreferencesDao
import com.lucdre.idleskills.profile.data.PreferencesEntity
import com.lucdre.idleskills.profile.data.ProfileDao
import com.lucdre.idleskills.profile.data.ProfileEntity
import com.lucdre.idleskills.skills.data.SkillDao
import com.lucdre.idleskills.skills.data.SkillEntity

/**
 * Main database for the application.
 */
@Database(
    entities = [
        ProfileEntity::class,
        PreferencesEntity::class,
        SessionEntity::class,
        SkillEntity::class,
        CardEntity::class,
        LootBoxEntity::class,
        InventoryEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun sessionDao(): SessionDao
    abstract fun skillDao(): SkillDao
    abstract fun cardDao(): CardDao
    abstract fun lootBoxDao(): LootBoxDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun progressApplicationDao(): ProgressApplicationDao
}
