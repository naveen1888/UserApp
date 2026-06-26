package com.user.app.features.user_management.data.datasource

import com.user.app.features.user_management.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for user data access
 * Abstracts database operations
 */
interface UserLocalDataSource {
    /**
     * Get all users from local database
     */
    fun getAllUsers(): Flow<List<User>>

    /**
     * Get user by ID from local database
     */
    fun getUserById(id: Int): Flow<User?>

    /**
     * Get user by email from local database
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Insert user into local database
     */
    suspend fun insertUser(user: User)
}

