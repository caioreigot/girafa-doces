package com.github.caioreigot.girafadoces.di

import com.github.caioreigot.girafadoces.data.remote.DatabaseService
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseServiceModule {

    @Provides
    fun provideDatabaseService(): DatabaseRepository = DatabaseService()
}