package com.user.app.features.user_management.data.datasource

import com.user.app.core.data.local.dao.UserDao
import com.user.app.core.data.local.entity.User as UserEntity
import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.data.mapper.toDomain
import com.user.app.features.user_management.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [UserLocalDataSource] using Room DAO.
 */
class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : UserLocalDataSource {

    override fun getAllUsers(): Flow<List<User>> {
        Timber.d("Fetching all users from local database")
        return userDao.getAllUsers()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getUserById(id: Int): Flow<User?> {
        Timber.d("Fetching user with id: $id from local database")
        return userDao.getUserById(id)
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun getUserByEmail(email: String): User? {
        Timber.d("Fetching user with email: $email from local database")
        val entity = userDao.getUserByEmail(email)
        return entity?.toDomain()
    }

    override suspend fun insertUser(user: User) {
        Timber.d("Inserting user: ${user.name} into local database")
        try {
            userDao.insertUser(user.toEntity())
            Timber.i("User inserted successfully: ${user.email}")
        } catch (e: Exception) {
            Timber.e(e, "Error inserting user")
            throw e
        }
    }
}
