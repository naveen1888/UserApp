package com.user.app.features.user_management.presentation.state

import com.user.app.core.error.AppError
import com.user.app.features.user_management.domain.model.User

/**
 * UI State for User List feature
 */
data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null
) {
    val isEmpty: Boolean get() = users.isEmpty() && !isLoading
}

/**
 * UI State for Add User feature
 */
data class AddUserState(
    val name: String = "",
    val email: String = "",
    val age: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val ageError: String? = null,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && age.isNotBlank() &&
                nameError == null && emailError == null && ageError == null

    val isActionEnabled: Boolean
        get() = !isLoading && isFormValid
}

/**
 * UI State for User Details feature
 */
data class UserDetailsState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null
) {
    val hasUser: Boolean get() = user != null
}

