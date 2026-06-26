package com.user.app.ui.userdetails

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.presentation.screen.UserDetailsContent
import org.junit.Rule
import org.junit.Test

class UserDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userDetailsContent_displaysUserInformation() {
        val user = User(id = 1, name = "Jane Doe", email = "jane@test.com", age = 28)

        composeTestRule.setContent {
            UserDetailsContent(user = user, onNavigateBack = {})
        }

        composeTestRule.onNodeWithText("Jane Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("jane@test.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("28").assertIsDisplayed()
    }

    @Test
    fun userDetailsContent_showsLoadingIndicator_whenUserIsNull() {
        composeTestRule.setContent {
            UserDetailsContent(user = null, isLoading = true, onNavigateBack = {})
        }

        // Verify that the loading indicator is displayed
        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
        
        // Verify that user details are NOT shown
        composeTestRule.onNodeWithText("Jane Doe").assertDoesNotExist()
    }

    @Test
    fun userDetailsContent_backButtonClick_triggersOnNavigateBack() {
        var backClicked = false

        composeTestRule.setContent {
            UserDetailsContent(
                user = User(1, "Jane", "jane@test.com", 28),
                onNavigateBack = { backClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }
}
