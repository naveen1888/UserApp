package com.user.app.features.auth.domain.repository

import com.user.app.features.auth.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface for authentication operations.
 * Defines contracts for auth-related data access without framework specifics.
 */
interface AuthRepository {
    /**
     * Attempts to login a user with username and password
     * @param username The user's username
     * @param password The user's password
     * @return Flow<LoginResult> emitting the login result
     */
    fun login(username: String, password: String): Flow<Result<LoginResult>>
}

