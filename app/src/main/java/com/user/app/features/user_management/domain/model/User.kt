package com.user.app.features.user_management.domain.model

/**
 * Domain model representing a User.
 * Independent of any framework or database implementation.
 */
data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val age: Int
)

