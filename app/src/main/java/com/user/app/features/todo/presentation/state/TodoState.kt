package com.user.app.features.todo.presentation.state

import com.user.app.core.error.AppError

/**
 * UI State for the Todo login screen.
 */
data class TodoState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val isLoggedIn: Boolean = false,
    val usernameError: String? = null
) {
    val isFormValid: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && usernameError == null

    val isActionEnabled: Boolean
        get() = !isLoading && isFormValid
}
