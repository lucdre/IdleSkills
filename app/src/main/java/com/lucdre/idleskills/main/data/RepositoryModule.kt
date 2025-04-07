package com.lucdre.idleskills.main.data

import com.lucdre.idleskills.skills.data.MockSkillRepository
import com.lucdre.idleskills.skills.domain.SkillRepository
import com.lucdre.idleskills.skills.woodcutting.data.WcTrainingMethodRepository
import com.lucdre.idleskills.skills.domain.TrainingMethodRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrainingMethodRepository(
        repository: WcTrainingMethodRepository
    ): TrainingMethodRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        repository: MockSkillRepository
    ): SkillRepository
}