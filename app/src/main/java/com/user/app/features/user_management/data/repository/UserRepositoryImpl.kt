package com.user.app.features.user_management.data.repository

import com.user.app.features.user_management.data.datasource.UserLocalDataSource
import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [UserRepository] using [UserLocalDataSource]
 */
class UserRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource
) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> {
        Timber.d("Repository: Getting all users")
        return localDataSource.getAllUsers()
    }

    override fun getUserById(id: Int): Flow<User?> {
        Timber.d("Repository: Getting user by id: $id")
        return localDataSource.getUserById(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        Timber.d("Repository: Getting user by email: $email")
        return localDataSource.getUserByEmail(email)
    }

    override suspend fun insertUser(user: User) {
        Timber.d("Repository: Inserting user: ${user.email}")
        localDataSource.insertUser(user)
    }
}

