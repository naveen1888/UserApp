package com.user.app.features.auth.di

import com.user.app.features.auth.data.remote.api.AuthService
import com.user.app.features.auth.data.datasource.AuthRemoteDataSource
import com.user.app.features.auth.data.datasource.AuthRemoteDataSourceImpl
import com.user.app.features.auth.data.repository.AuthRepositoryImpl
import com.user.app.features.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module for Auth feature dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthFeatureDiModule {

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthService(retrofit: Retrofit): AuthService {
            return retrofit.create(AuthService::class.java)
        }
    }
}
