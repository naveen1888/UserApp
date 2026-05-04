package com.user.app.ui.userdetails

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.user.app.data.local.entity.User
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

        composeTestRule.onNodeWithText("Name: Jane Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email: jane@test.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Age: 28").assertIsDisplayed()
    }

    @Test
    fun userDetailsContent_showsLoadingIndicator_whenUserIsNull() {
        composeTestRule.setContent {
            UserDetailsContent(user = null, onNavigateBack = {})
        }

        // Verify that the loading indicator is displayed using its test tag
        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
        
        // Verify that user details are NOT shown
        composeTestRule.onNodeWithText("Name:", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("Email:", substring = true).assertDoesNotExist()
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
