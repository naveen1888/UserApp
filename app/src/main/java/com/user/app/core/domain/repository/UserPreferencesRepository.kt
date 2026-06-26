package com.user.app.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface defining user preferences operations for the domain layer.
 */
interface UserPreferencesRepository {
    val isLoggedIn: Flow<Boolean>
    val userEmail: Flow<String?>
    suspend fun saveLoginStatus(isLoggedIn: Boolean, email: String?)
}
