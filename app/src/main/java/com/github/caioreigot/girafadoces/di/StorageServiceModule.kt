package com.github.caioreigot.girafadoces.di

import com.github.caioreigot.girafadoces.data.remote.StorageService
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StorageServiceModule {

    @Provides
    fun provideStorageService(): StorageRepository = StorageService()
}