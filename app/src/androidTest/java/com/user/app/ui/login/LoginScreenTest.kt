package com.user.app.ui.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.user.app.features.auth.presentation.screen.LoginContent
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysFieldsAndButton() {
        composeTestRule.setContent {
            LoginContent(
                username = "",
                password = "",
                isLoggingIn = false,
                error = null,
                onUsernameChanged = {},
                onPasswordChanged = {},
                onLogin = {},
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
                username = "test@example.com",
                password = "password",
                isLoggingIn = false,
                error = null,
                onUsernameChanged = {},
                onPasswordChanged = {},
                onLogin = { loginTriggered = true },
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("LoginButton").performClick()
        assert(loginTriggered)
    }
}
