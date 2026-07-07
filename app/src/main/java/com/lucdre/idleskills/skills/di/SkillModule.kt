@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.skills.di

import com.lucdre.idleskills.core.persistence.AppDatabase
import com.lucdre.idleskills.skills.data.SkillDao
import com.lucdre.idleskills.skills.data.SkillRepository
import com.lucdre.idleskills.skills.domain.skill.SkillRepositoryInterface
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryDispatcher
import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SkillModule {

    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        repository: SkillRepository
    ): SkillRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindTrainingMethodRepositoryDispatcher(
        dispatcher: TrainingMethodRepositoryDispatcher
    ): TrainingMethodRepositoryInterface

    companion object {
        @Provides
        fun provideSkillDao(db: AppDatabase): SkillDao = db.skillDao()
    }
}
