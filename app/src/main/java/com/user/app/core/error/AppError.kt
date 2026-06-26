package com.user.app.core.error

/**
 * Domain layer error representation.
 * All errors are converted to this sealed class for consistent handling across the app.
 */
sealed class AppError {
    data object NetworkError : AppError()
    data object DatabaseError : AppError()
    data class ValidationError(val message: String) : AppError()
    data class ServerError(val code: Int, val message: String?) : AppError()
    data class UnknownError(val exception: Throwable) : AppError()

    fun getDisplayMessage(): String = when (this) {
        is NetworkError -> "A network connection error occurred"
        is DatabaseError -> "Database error occurred. Please try again"
        is ValidationError -> message
        is ServerError -> message ?: "Server error occurred (Code: $code)"
        is UnknownError -> "An unexpected error occurred: ${exception.message}"
    }
}

