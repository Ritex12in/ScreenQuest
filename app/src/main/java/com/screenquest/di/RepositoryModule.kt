package com.screenquest.di

import com.screenquest.data.repository.GameRepositoryImpl
import com.screenquest.data.repository.UsageRepositoryImpl
import com.screenquest.domain.repository.IGameRepository
import com.screenquest.domain.repository.IUsageRepository
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
    abstract fun bindUsageRepository(
        impl: UsageRepositoryImpl
    ): IUsageRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        impl: GameRepositoryImpl
    ): IGameRepository
}