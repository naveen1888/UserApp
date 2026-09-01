package com.user.app.features.todo.presentation.intent

/**
 * User Intents for the Todo feature login screen.
 */
sealed class TodoIntent {
    data class OnUsernameChanged(val username: String) : TodoIntent()
    data class OnPasswordChanged(val password: String) : TodoIntent()
    data object OnLoginClicked : TodoIntent()
    data object OnClearError : TodoIntent()
}
