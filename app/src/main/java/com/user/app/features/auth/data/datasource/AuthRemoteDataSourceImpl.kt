package com.user.app.features.auth.data.datasource

import com.user.app.features.auth.data.remote.api.AuthService
import com.user.app.features.auth.data.remote.model.LoginRequest
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [AuthRemoteDataSource] using Retrofit
 */
class AuthRemoteDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : AuthRemoteDataSource {

    override suspend fun login(username: String, password: String): Result<LoginResponseDto> {
        return try {
            val response = authService.login(
                LoginRequest(
                    userName = username,
                    password = password
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(
                        LoginResponseDto(
                            id = body.id,
                            username = body.username,
                            email = body.email,
                            firstName = body.firstName,
                            lastName = body.lastName,
                            gender = body.gender,
                            image = body.image,
                            accessToken = body.accessToken,
                            refreshToken = body.refreshToken,
                            status = body.status,
                            message = body.message
                        )
                    )
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Timber.w("Login failed with status ${response.code()}")
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during login")
            Result.failure(e)
        }
    }
}
