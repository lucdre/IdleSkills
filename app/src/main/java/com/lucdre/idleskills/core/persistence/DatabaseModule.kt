package com.lucdre.idleskills.core.persistence

import android.content.Context
import androidx.room3.Room
import com.lucdre.idleskills.core.domain.GameActionRepository
import com.lucdre.idleskills.core.domain.GameActionRepositoryInterface
import com.lucdre.idleskills.core.domain.SessionRepository
import com.lucdre.idleskills.core.domain.SessionRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        repository: SessionRepository
    ): SessionRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindGameActionRepository(
        repository: GameActionRepository
    ): GameActionRepositoryInterface

    companion object {
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
        fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()

        @Provides
        fun provideProgressApplicationDao(db: AppDatabase): ProgressApplicationDao = db.progressApplicationDao()
    }
}
