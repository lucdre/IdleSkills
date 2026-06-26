package com.lucdre.idleskills.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Module responsible for providing [Random] instance for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object RandomModule {

    @Provides
    @Singleton
    fun provideRandom(): Random = Random.Default
}
