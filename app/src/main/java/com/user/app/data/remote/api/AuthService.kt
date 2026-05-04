package com.user.app.data.remote.api

import com.user.app.data.remote.model.LoginRequest
import com.user.app.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for authentication-related API calls.
 */
interface AuthService {

    /**
     * Performs a login request.
     * 
     * @param request The login request body containing userName and password
     * @return Retrofit Response containing LoginResponse
     */

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
