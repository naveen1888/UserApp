package com.user.app.core.domain.usecase

import com.user.app.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get the current login status.
 */
class GetLoginStatusUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn
}

/**
 * Use case to save the login status and user email.
 */
class SaveLoginStatusUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(isLoggedIn: Boolean, email: String? = null) {
        repository.saveLoginStatus(isLoggedIn, email)
    }
}

/**
 * Use case to get the stored user email.
 */
class GetUserEmailUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String?> = repository.userEmail
}
