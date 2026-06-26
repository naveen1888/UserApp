package com.user.app.util

import com.user.app.core.util.Constants
import com.user.app.core.util.InputSanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputSanitizerTest {

    @Test
    fun sanitizeName_removesLeadingWhitespace() {
        val input = "  John Doe"
        val expected = "John Doe"
        assertEquals(expected, InputSanitizer.sanitizeName(input))
    }

    @Test
    fun sanitizeName_collapsesMultipleSpaces() {
        val input = "John    Doe"
        val expected = "John Doe"
        assertEquals(expected, InputSanitizer.sanitizeName(input))
    }

    @Test
    fun sanitizeName_keepsAccentedCharacters() {
        val input = "José María"
        val expected = "José María"
        assertEquals(expected, InputSanitizer.sanitizeName(input))
    }

    @Test
    fun sanitizeEmail_trimsAndLowercases() {
        val input = "  Test@Example.Com  "
        val expected = "test@example.com"
        assertEquals(expected, InputSanitizer.sanitizeEmail(input))
    }

    @Test
    fun sanitizeEmail_removesInternalWhitespace() {
        val input = "test @ example . com"
        val expected = "test@example.com"
        assertEquals(expected, InputSanitizer.sanitizeEmail(input))
    }

    @Test
    fun sanitizeAge_removesNonDigits() {
        val input = "age 25!"
        val expected = "25"
        assertEquals(expected, InputSanitizer.sanitizeAge(input))
    }

    @Test
    fun validateEmail_returnsNullForValidEmail() {
        val email = "valid@example.com"
        assertNull(InputSanitizer.validateEmail(email))
    }

    @Test
    fun validateEmail_returnsErrorForBlankEmail() {
        assertEquals(Constants.ERROR_EMAIL_EMPTY, InputSanitizer.validateEmail(""))
        assertEquals(Constants.ERROR_EMAIL_EMPTY, InputSanitizer.validateEmail("   "))
    }

    @Test
    fun validateEmail_returnsErrorForInvalidFormat() {
        assertEquals(Constants.ERROR_EMAIL_INVALID, InputSanitizer.validateEmail("invalid-email"))
        assertEquals(Constants.ERROR_EMAIL_INVALID, InputSanitizer.validateEmail("test@"))
        assertEquals(Constants.ERROR_EMAIL_INVALID, InputSanitizer.validateEmail("@example.com"))
    }
}
