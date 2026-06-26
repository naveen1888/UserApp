package com.user.app.features.auth.data.datasource

/**
 * Remote data source for authentication operations.
 * Abstracts network calls for login operations.
 */
interface AuthRemoteDataSource {
    /**
     * Performs login request to remote server
     * @param username The user's username
     * @param password The user's password
     * @return Result of login attempt with response data
     */
    suspend fun login(username: String, password: String): Result<LoginResponseDto>
}

/**
 * DTO for API login response
 */
data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val accessToken: String,
    val refreshToken: String,
    val status: String? = null,
    val message: String? = null
)

