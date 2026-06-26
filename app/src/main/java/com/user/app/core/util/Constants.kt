package com.user.app.core.util

/**
 * Application-wide constants
 */
object Constants {
    // API
    const val BASE_URL = "https://dummyjson.com/"

    // Database
    const val DATABASE_NAME = "user_database"

    // User validation constraints
    const val MIN_AGE = 1
    const val MAX_AGE = 100
    const val MAX_NAME_LENGTH = 100
    const val MAX_EMAIL_LENGTH = 255

    /**
     * Improved regex for email validation (RFC 5322 simplified).
     * Validates basic email format: localpart@domain.tld
     */
    val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$"
    )

    // Validation messages
    const val ERROR_NAME_EMPTY = "Name cannot be empty"
    const val ERROR_NAME_TOO_LONG = "Name must be less than $MAX_NAME_LENGTH characters"
    const val ERROR_NAME_INVALID = "Name can only contain letters and spaces"
    const val ERROR_EMAIL_EMPTY = "Email cannot be empty"
    const val ERROR_USERNAME_EMPTY = "Username cannot be empty"
    const val ERROR_EMAIL_INVALID = "Invalid email format"
    const val ERROR_EMAIL_TOO_LONG = "Email must be less than $MAX_EMAIL_LENGTH characters"
    const val ERROR_AGE_EMPTY = "Age cannot be empty"
    const val ERROR_AGE_INVALID = "Age must contain only numbers"
    const val ERROR_AGE_OUT_OF_RANGE = "Enter a valid age ($MIN_AGE-$MAX_AGE)"

    // Empty state message
    const val EMPTY_USER_LIST_MESSAGE = "No users found. Tap + to add one."
}
