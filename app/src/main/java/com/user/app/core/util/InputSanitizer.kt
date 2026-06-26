package com.user.app.core.util

/**
 * Utility functions for input sanitization and validation.
 */
object InputSanitizer {
    
    /**
     * Sanitizes a name input by:
     * - Preventing leading whitespace
     * - Collapsing multiple consecutive spaces to single space
     * 
     * @param input The raw name input
     * @return Sanitized name string
     */
    fun sanitizeName(input: String): String {
        return input
            .trimStart() // No white space before the name starts
            .replace(Regex("\\s+"), " ")  // Collapse multiple spaces to single space
    }
    
    /**
     * Sanitizes an email input by:
     * - Trimming leading/trailing whitespace
     * - Converting to lowercase
     * - Removing all internal whitespace
     * 
     * @param input The raw email input
     * @return Sanitized email string
     */
    fun sanitizeEmail(input: String): String {
        return input
            .trim()
            .lowercase()
            .filter { !it.isWhitespace() }
    }
    
    /**
     * Sanitizes an age input by:
     * - Trimming whitespace
     * - Removing all non-digit characters
     * 
     * @param input The raw age input
     * @return Sanitized age string containing only digits
     */
    fun sanitizeAge(input: String): String {
        return input
            .trim()
            .filter { it.isDigit() }
    }

    /**
     * Validates an email address.
     * 
     * @param value The email to validate
     * @return Error message if invalid, null if valid
     */
    fun validateEmail(value: String): String? {
        return when {
            value.isBlank() -> Constants.ERROR_EMAIL_EMPTY
            value.length > Constants.MAX_EMAIL_LENGTH -> Constants.ERROR_EMAIL_TOO_LONG
            !Constants.EMAIL_REGEX.matches(value) -> Constants.ERROR_EMAIL_INVALID
            else -> null
        }
    }
}
