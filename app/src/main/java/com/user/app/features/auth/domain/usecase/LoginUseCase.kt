package com.user.app.features.auth.domain.usecase

import com.user.app.features.auth.domain.model.LoginResult
import com.user.app.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for user login operation.
 * Handles authorization logic independently of framework details.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute login with username and password
     * Returns Result<LoginResult> as Flow for reactive handling
     */
    operator fun invoke(username: String, password: String): Flow<Result<LoginResult>> {
        return authRepository.login(username, password)
    }
}

