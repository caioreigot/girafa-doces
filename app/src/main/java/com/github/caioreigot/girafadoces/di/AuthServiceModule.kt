package com.github.caioreigot.girafadoces.di

import com.github.caioreigot.girafadoces.data.remote.AuthService
import com.github.caioreigot.girafadoces.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthServiceModule {

    @Provides
    fun provideAuthService(): AuthRepository = AuthService()
}