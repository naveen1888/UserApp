package com.user.app.data.repository

import com.user.app.data.remote.api.AuthService
import com.user.app.data.remote.model.LoginRequest
import com.user.app.data.remote.model.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository class for handling authentication operations.
 * Marked as open to allow mocking in instrumented tests.
 */
open class AuthRepository @Inject constructor(
    private val authService: AuthService
) {

    /**
     * Performs a login request via the remote API.
     *
     * @param request The login request containing userName and password
     * @return Retrofit Response containing LoginResponse
     */
    open suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return authService.login(request)
    }

    open fun login2(request: LoginRequest): Flow<Response<LoginResponse>> =
        flow {
        // This is where the actual suspension and network call happens
        emit(authService.login(request))
    }

}