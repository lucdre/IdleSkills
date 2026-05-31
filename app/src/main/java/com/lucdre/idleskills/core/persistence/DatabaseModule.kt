package com.lucdre.idleskills.core.persistence

import android.content.Context
import androidx.room3.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "idleskills.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()

    @Provides
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()

    @Provides
    fun providePrestigeDao(db: AppDatabase): PrestigeDao = db.prestigeDao()

    @Provides
    fun provideSkillDao(db: AppDatabase): SkillDao = db.skillDao()

    @Provides
    fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()

    @Provides
    fun provideLootBoxDao(db: AppDatabase): LootBoxDao = db.lootBoxDao()

    @Provides
    fun provideOfflineProgressDao(db: AppDatabase): OfflineProgressDao = db.offlineProgressDao()
}
