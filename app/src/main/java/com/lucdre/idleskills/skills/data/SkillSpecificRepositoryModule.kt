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
 * Dagger/Hilt module for binding skill-specific training method repositories into a map.
 * This allows the dispatcher to resolve repositories dynamically based on skill names.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SkillSpecificRepositoryModule {

    @Binds
    @IntoMap
    @StringKey("Woodcutting")
    abstract fun bindWcRepository(
        repository: WcTrainingMethodRepository
    ): TrainingMethodRepositoryInterface

    @Binds
    @IntoMap
    @StringKey("Mining")
    abstract fun bindMiningRepository(
        repository: MiningTrainingMethodRepository
    ): TrainingMethodRepositoryInterface

    @Binds
    @IntoMap
    @StringKey("Fishing")
    abstract fun bindFishingRepository(
        repository: FishingTrainingMethodRepository
    ): TrainingMethodRepositoryInterface
}
