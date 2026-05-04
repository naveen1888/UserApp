package com.user.app.ui.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysFieldsAndButton() {
        composeTestRule.setContent {
            LoginContent(
                isLoggingIn = false,
                error = null,
                onLogin = { _, _ -> },
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("UsernameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("PasswordInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("LoginButton").assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickLogin_triggersCallback() {
        var loginTriggered = false
        composeTestRule.setContent {
            LoginContent(
                isLoggingIn = false,
                error = null,
                onLogin = { _, _ -> loginTriggered = true },
                onClearError = {}
            )
        }

        // Fill in a username/email to pass validation
        composeTestRule.onNodeWithTag("UsernameInput").performTextInput("test@example.com")

        composeTestRule.onNodeWithTag("LoginButton").performClick()
        assert(loginTriggered)
    }
}
