package com.user.app.features.user_management.di

import com.user.app.core.data.local.dao.UserDao
import com.user.app.core.data.local.db.UserDatabase
import com.user.app.features.user_management.data.datasource.UserLocalDataSource
import com.user.app.features.user_management.data.datasource.UserLocalDataSourceImpl
import com.user.app.features.user_management.data.repository.UserRepositoryImpl
import com.user.app.features.user_management.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for User Management feature dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UserManagementFeatureDiModule {

    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(
        impl: UserLocalDataSourceImpl
    ): UserLocalDataSource

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserDao(database: UserDatabase): UserDao {
            return database.userDao()
        }
    }
}
