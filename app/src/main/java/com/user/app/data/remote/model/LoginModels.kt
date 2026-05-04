package com.user.app.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the request body for the login API.
 * 
 * @property userName The user's name or email
 * @property password The user's password
 */
data class LoginRequest(
    @SerializedName("username")
    val userName: String,
    @SerializedName("password")
    val password: String
)

/**
 * Data class representing the response body from the login API (DummyJSON format).
 * 
 * Example Response:
 * ```json
 * {
 *   "accessToken": "eyJhbGciOi...",
 *   "refreshToken": "eyJhbGciOi...",
 *   "id": 1,
 *   "username": "emilys",
 *   "email": "emily.johnson@x.dummyjson.com",
 *   "firstName": "Emily",
 *   "lastName": "Johnson",
 *   "gender": "female",
 *   "image": "https://dummyjson.com/icon/emilys/128"
 * }
 * ```
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
    // Keep these as optional for backward compatibility or if wrapped
    val status: String? = null,
    val message: String? = null
)
