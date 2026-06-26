package com.user.app.features.auth.data.remote.api

import com.user.app.features.auth.data.remote.model.LoginRequest
import com.user.app.features.auth.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for authentication-related API calls.
 */
interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
