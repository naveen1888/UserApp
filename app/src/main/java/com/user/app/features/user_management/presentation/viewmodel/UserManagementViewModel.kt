package com.user.app.features.user_management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.user.app.core.error.AppError
import com.user.app.core.util.Constants
import com.user.app.core.util.InputSanitizer
import com.user.app.features.user_management.domain.usecase.AddUserUseCase
import com.user.app.features.user_management.domain.usecase.GetAllUsersUseCase
import com.user.app.features.user_management.domain.usecase.GetUserByIdUseCase
import com.user.app.features.user_management.presentation.state.AddUserState
import com.user.app.features.user_management.presentation.state.UserDetailsState
import com.user.app.features.user_management.presentation.state.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for User Management feature.
 * Handles user list, add user, and user details operations.
 * Supports MVVM pattern with unidirectional data flow.
 */
@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val addUserUseCase: AddUserUseCase
) : ViewModel() {

    // User List State
    private val _userListState = MutableStateFlow(UserListState())
    val userListState: StateFlow<UserListState> = _userListState.asStateFlow()

    // Add User State
    private val _addUserState = MutableStateFlow(AddUserState())
    val addUserState: StateFlow<AddUserState> = _addUserState.asStateFlow()

    // User Details State (by ID)
    private val _userDetailsStates = mutableMapOf<Int, MutableStateFlow<UserDetailsState>>()

    init {
        loadAllUsers()
    }

    /**
     * Load all users
     */
    private fun loadAllUsers() {
        Timber.d("Loading all users")
        getAllUsersUseCase()
            .onEach { users ->
                Timber.d("Users loaded: ${users.size} users")
                _userListState.update { state ->
                    state.copy(
                        users = users.sortedBy { it.name },
                        isLoading = false,
                        error = null
                    )
                }
            }
            .catch { error ->
                Timber.e(error, "Error loading users")
                _userListState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = mapExceptionToError(error)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Get user details for a specific ID
     */
    fun getUserDetails(userId: Int): StateFlow<UserDetailsState> {
        if (!_userDetailsStates.containsKey(userId)) {
            val stateFlow = MutableStateFlow(UserDetailsState(isLoading = true))
            _userDetailsStates[userId] = stateFlow

            Timber.d("Loading user details for id: $userId")
            getUserByIdUseCase(userId)
                .onEach { user ->
                    Timber.d("User details loaded: ${user?.name}")
                    stateFlow.update { state ->
                        state.copy(
                            user = user,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .catch { error ->
                    Timber.e(error, "Error loading user details")
                    stateFlow.update { state ->
                        state.copy(
                            isLoading = false,
                            error = mapExceptionToError(error)
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
        return _userDetailsStates[userId]!!.asStateFlow()
    }

    /**
     * Update name in add user form
     */
    fun onNameChanged(name: String) {
        val sanitized = InputSanitizer.sanitizeName(name)
        val error = validateName(sanitized)
        _addUserState.update { state ->
            state.copy(
                name = sanitized,
                nameError = error
            )
        }
    }

    /**
     * Update email in add user form
     */
    fun onEmailChanged(email: String) {
        val sanitized = InputSanitizer.sanitizeEmail(email)
        val error = InputSanitizer.validateEmail(sanitized)
        _addUserState.update { state ->
            state.copy(
                email = sanitized,
                emailError = error
            )
        }
    }

    /**
     * Update age in add user form
     */
    fun onAgeChanged(age: String) {
        val sanitized = InputSanitizer.sanitizeAge(age)
        val error = validateAge(sanitized)
        _addUserState.update { state ->
            state.copy(
                age = sanitized,
                ageError = error
            )
        }
    }

    /**
     * Add a new user
     */
    fun addUser() {
        val currentState = _addUserState.value

        if (!currentState.isFormValid) {
            Timber.w("Invalid form state for add user")
            return
        }

        _addUserState.update { it.copy(isLoading = true, error = null) }
        Timber.d("Starting add user process for email: ${currentState.email}")

        viewModelScope.launch {
            addUserUseCase(
                currentState.name,
                currentState.email,
                currentState.age.toIntOrNull() ?: 0
            ).onSuccess {
                Timber.i("User successfully added: ${currentState.email}")
                _addUserState.update { state ->
                    state.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }
            }.onFailure { exception ->
                Timber.e(exception, "Error adding user")
                val error = if (exception.message == "DuplicateEmail") {
                    AppError.ValidationError("Email address is already in use.")
                } else {
                    AppError.DatabaseError
                }
                _addUserState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = error,
                        isSuccess = false
                    )
                }
            }
        }
    }

    /**
     * Reset add user form
     */
    fun resetAddUserForm() {
        _addUserState.value = AddUserState()
    }

    /**
     * Clear error from add user state
     */
    fun clearAddUserError() {
        _addUserState.update { it.copy(error = null) }
    }

    /**
     * Validate name field
     */
    private fun validateName(value: String): String? {
        return when {
            value.isEmpty() -> Constants.ERROR_NAME_EMPTY
            value.length > Constants.MAX_NAME_LENGTH -> Constants.ERROR_NAME_TOO_LONG
            !value.matches(Regex("^[a-zA-Z\\s]+$")) -> Constants.ERROR_NAME_INVALID
            else -> null
        }
    }

    /**
     * Validate age field
     */
    private fun validateAge(value: String): String? {
        return when {
            value.isEmpty() -> Constants.ERROR_AGE_EMPTY
            !value.matches(Regex("^\\d+$")) -> Constants.ERROR_AGE_INVALID
            else -> {
                val age = value.toIntOrNull()
                if (age != null && (age < Constants.MIN_AGE || age > Constants.MAX_AGE)) {
                    Constants.ERROR_AGE_OUT_OF_RANGE
                } else {
                    null
                }
            }
        }
    }

    /**
     * Map exception to AppError for display
     */
    private fun mapExceptionToError(exception: Throwable): AppError {
        return when {
            exception.message?.contains("duplicate", ignoreCase = true) == true ->
                AppError.ValidationError("Email address already exists")

            exception.message?.contains("Database", ignoreCase = true) == true ->
                AppError.DatabaseError

            exception.message?.contains("Network", ignoreCase = true) == true ->
                AppError.NetworkError

            else -> AppError.UnknownError(exception)
        }
    }
}
