package com.user.app.features.auth.domain.model

/**
 * Domain model for Login response
 */
data class LoginResult(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val token: String
)

