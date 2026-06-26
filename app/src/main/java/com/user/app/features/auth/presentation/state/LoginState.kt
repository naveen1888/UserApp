package com.user.app.features.auth.presentation.state

import com.user.app.core.error.AppError
import com.user.app.features.auth.domain.model.LoginResult

/**
 * UI State for Login feature
 * Encapsulates all possible states during login operation
 */
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val loginResult: LoginResult? = null,
    val isLoggedIn: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null
) {
    val isFormValid: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && usernameError == null && passwordError == null

    val isActionEnabled: Boolean
        get() = !isLoading && isFormValid
}

