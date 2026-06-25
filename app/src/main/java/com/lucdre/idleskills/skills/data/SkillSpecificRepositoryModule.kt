@file:Suppress("UNUSED") // Hilt dependency injection

package com.lucdre.idleskills.skills.data

import com.lucdre.idleskills.skills.domain.training.TrainingMethodRepositoryInterface
import com.lucdre.idleskills.skills.fishing.data.FishingTrainingMethodRepository
import com.lucdre.idleskills.skills.mining.data.MiningTrainingMethodRepository
import com.lucdre.idleskills.skills.woodcutting.data.WcTrainingMethodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

/**
 * Hilt module for skill-specific training repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SkillSpecificRepositoryModule {

    @Binds
    @IntoMap
    @StringKey("WOODCUTTING")
    abstract fun bindWcRepository(
        repository: WcTrainingMethodRepository
    ): TrainingMethodRepositoryInterface

    @Binds
    @IntoMap
    @StringKey("MINING")
    abstract fun bindMiningRepository(
        repository: MiningTrainingMethodRepository
    ): TrainingMethodRepositoryInterface

    @Binds
    @IntoMap
    @StringKey("FISHING")
    abstract fun bindFishingRepository(
        repository: FishingTrainingMethodRepository
    ): TrainingMethodRepositoryInterface
}
