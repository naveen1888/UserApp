package com.user.app.features.user_management.domain.repository

import com.user.app.features.user_management.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface for user management operations.
 * Defines contracts without framework specifics.
 */
interface UserRepository {
    /**
     * Get all users
     */
    fun getAllUsers(): Flow<List<User>>

    /**
     * Get a specific user by ID
     */
    fun getUserById(id: Int): Flow<User?>

    /**
     * Get user by email
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Insert a new user
     */
    suspend fun insertUser(user: User)
}

