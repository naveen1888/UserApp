package com.user.app.features.auth.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the request body for the login API.
 */
data class LoginRequest(
    @SerializedName("username")
    val userName: String,
    @SerializedName("password")
    val password: String
)

/**
 * Data class representing the response body from the login API (DummyJSON format).
 */
data class LoginResponse(
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
