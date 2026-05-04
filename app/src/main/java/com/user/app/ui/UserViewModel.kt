package com.user.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.user.app.data.local.entity.User
import com.user.app.data.remote.model.LoginRequest
import com.user.app.data.remote.model.LoginResponse
import com.user.app.data.repository.AuthRepository
import com.user.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * Sealed class representing different error types that can occur during user operations.
 * 
 * Each error type provides a user-friendly display message via [getDisplayMessage].
 */
sealed class UserError {
    
    /** Occurs when attempting to add a user with an email that already exists */
    data object DuplicateEmail : UserError()
    
    /** Occurs when a database operation fails */
    data object DatabaseError : UserError()
    
    /** Occurs when a network error happens */
    data object NetworkError : UserError()
    
    /** Generic error with custom message */
    data class UnknownError(val message: String?) : UserError()

    /**
     * Provides a user-friendly error message for display in the UI.
     * 
     * @return Localized error message string
     */
    fun getDisplayMessage(): String {
        return when (this) {
            is DuplicateEmail -> "Email address is already in use."
            is DatabaseError -> "A database error occurred while saving. Please try again."
            is NetworkError -> "A network connection error occurred."
            is UnknownError -> message ?: "An unexpected error occurred."
        }
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    /** StateFlow indicating whether a save operation is in progress */
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    /** StateFlow indicating whether a login operation is in progress */
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

    private val _error = MutableStateFlow<UserError?>(null)
    /** StateFlow emitting the current error state, or null if no error */
    val error: StateFlow<UserError?> = _error.asStateFlow()

    private var saveJob: Job? = null
    private var loginJob: Job? = null

    /**
     * StateFlow containing the list of all users, ordered by name.
     * Emits empty list initially and updates whenever the database changes.
     */
    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Gets a Flow of a specific user by ID.
     * 
     * @param id The user ID to retrieve
     * @return Flow emitting the user or null if not found
     */
    fun getUserById(id: Int) = repository.getUserById(id)

    /**
     * Adds a new user to the database.
     * 
     * Performs validation to ensure email uniqueness and calls [onComplete] callback
     * upon successful insertion. Prevents concurrent operations.
     * 
     * @param name User's full name
     * @param email User's email (must be unique)
     * @param age User's age
     * @param onComplete Callback invoked when user is successfully saved
     */
    fun addUser(name: String, email: String, age: Int, onComplete: () -> Unit) {
        if (_isSaving.value) {
            Timber.d("addUser called but save already in progress")
            return
        }
        
        Timber.d("Adding user: name=$name, email=$email, age=$age")
        _error.value = null
        _isSaving.value = true
        saveJob = viewModelScope.launch {
            try {
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    Timber.w("Email already exists: $email")
                    _error.value = UserError.DuplicateEmail
                } else {
                    repository.insertUser(User(name = name, email = email, age = age))
                    Timber.i("User successfully added: $email")
                    onComplete()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error adding user")
                _error.value = UserError.DatabaseError
            } finally {
                _isSaving.value = false
            }
        }
    }

    /**
     * Performs a login request.
     * 
     * @param request The login request containing userName and password
     * @param onComplete Callback invoked when login is successful, passing the userName
     */
    fun login(request: LoginRequest, onComplete: (String) -> Unit) {
        if (_isLoggingIn.value) return

        _error.value = null
        _isLoggingIn.value = true
        loginJob = viewModelScope.launch {
            try {
                val response: Response<LoginResponse> = authRepository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    Timber.i("Login successful: ${request.userName}")
                    onComplete(request.userName)
                } else {
                    Timber.w("Login failed: ${response.code()}")
                    val message = "Invalid username or password"
                    _error.value = UserError.UnknownError(message)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error during login")
                _error.value = UserError.NetworkError
            } finally {
                _isLoggingIn.value = false
            }
        }
    }

    fun login2(request: LoginRequest, onComplete: (String) -> Unit) {
        if (_isLoggingIn.value) return

        _error.value = null
        _isLoggingIn.value = true

        // loginJob handles the collection of the Flow
        loginJob = viewModelScope.launch {
            authRepository.login2(request) // Now returns a Flow
                .collect { response -> // Collect the result
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            Timber.i("Login successful: ${request.userName}")
                            onComplete(request.userName)
                        } else {
                            Timber.w("Login failed: ${response.code()}")
                            val message = "Invalid username or password"
                            _error.value = UserError.UnknownError(message)
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing login response")
                        _error.value = UserError.NetworkError
                    } finally {
                        _isLoggingIn.value = false
                    }
                }
        }
    }

    /**
     * Clears the current error state.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Cancels any ongoing operations and resets state.
     */
    fun stopAllOperations() {
        Timber.d("Stopping all operations")
        saveJob?.cancel()
        loginJob?.cancel()
        _isSaving.value = false
        _isLoggingIn.value = false
        _error.value = null
    }
}
