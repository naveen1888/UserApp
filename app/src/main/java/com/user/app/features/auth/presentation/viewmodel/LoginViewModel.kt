package com.user.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.user.app.core.domain.usecase.SaveLoginStatusUseCase
import com.user.app.core.error.AppError
import com.user.app.core.util.InputSanitizer
import com.user.app.features.auth.domain.usecase.LoginUseCase
import com.user.app.features.auth.presentation.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Login feature.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveLoginStatusUseCase: SaveLoginStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
        val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onUsernameChanged(username: String) {
        val sanitized = InputSanitizer.sanitizeName(username)
        _state.update { it.copy(username = sanitized, usernameError = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun login() {
        val currentState = _state.value
        if (!currentState.isFormValid) return

        _state.update { it.copy(isLoading = true, error = null) }

        loginUseCase(currentState.username, currentState.password)
            .onEach { result ->
                if (result.isSuccess) {
                    val loginResult = result.getOrThrow()
                    // Save session automatically on success
                    saveLoginStatusUseCase(true, loginResult.email)
                    
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            loginResult = loginResult,
                            isLoggedIn = true,
                            error = null
                        )
                    }
                } else {
                    val exception = result.exceptionOrNull()!!
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            error = mapExceptionToError(exception),
                            isLoggedIn = false
                        )
                    }
                }
            }
            .catch { exception ->
                _state.update { state ->
                    state.copy(isLoading = false, error = AppError.UnknownError(exception))
                }
            }
            .launchIn(viewModelScope)
    }

    private fun mapExceptionToError(exception: Throwable): AppError {
        return when {
            exception.message?.contains("401") == true -> AppError.ValidationError("Invalid credentials")
            else -> AppError.NetworkError
        }
    }
}
