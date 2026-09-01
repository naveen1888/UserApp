package com.user.app.features.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.user.app.core.error.AppError
import com.user.app.core.util.InputSanitizer
import com.user.app.features.auth.domain.usecase.LoginUseCase
import com.user.app.features.todo.presentation.intent.TodoIntent
import com.user.app.features.todo.presentation.state.TodoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()

    fun processIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.OnUsernameChanged -> {
                val sanitized = InputSanitizer.sanitizeName(intent.username)
                _state.update { it.copy(username = sanitized, usernameError = null) }
            }
            is TodoIntent.OnPasswordChanged -> {
                _state.update { it.copy(password = intent.password) }
            }
            TodoIntent.OnLoginClicked -> login()
            TodoIntent.OnClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun login() {
        val currentState = _state.value
        if (!currentState.isFormValid) return

        _state.update { it.copy(isLoading = true, error = null) }

        loginUseCase(currentState.username, currentState.password)
            .onEach { result ->
                if (result.isSuccess) {
                    _state.update { it.copy(isLoading = false, isLoggedIn = true, error = null) }
                } else {
                    val exception = result.exceptionOrNull()!!
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = mapExceptionToError(exception),
                            isLoggedIn = false
                        )
                    }
                }
            }
            .catch { exception ->
                _state.update { it.copy(isLoading = false, error = AppError.UnknownError(exception)) }
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
