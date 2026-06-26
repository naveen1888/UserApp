package com.user.app.ui.adduser

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.user.app.features.user_management.presentation.screen.AddUserContent
import org.junit.Rule
import org.junit.Test

class AddUserScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addUserContent_displaysInputFields() {
        composeTestRule.setContent {
            AddUserContent(
                name = "",
                email = "",
                age = "",
                isLoading = false,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("NameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EmailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AgeInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SaveButton").assertIsDisplayed()
    }

    @Test
    fun addUserContent_showsErrors_whenInputsAreEmpty() {
        composeTestRule.setContent {
            AddUserContent(
                name = "",
                email = "",
                age = "",
                isLoading = false,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("SaveButton").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Age cannot be empty").assertIsDisplayed()
    }

    @Test
    fun addUserContent_showsError_whenNameIsInvalid() {
        composeTestRule.setContent {
            AddUserContent(
                name = "John123",
                email = "john@example.com",
                age = "25",
                isLoading = false,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("SaveButton").performClick()
        
        composeTestRule.onNodeWithText("Name can only contain letters and spaces").assertIsDisplayed()
    }

    @Test
    fun addUserContent_acceptsUnicodeCharactersInName() {
        var nameResult = ""
        var emailResult = ""
        var ageResult = 0

        composeTestRule.setContent {
            AddUserContent(
                name = "",
                email = "",
                age = "",
                isLoading = false,
                onNameChanged = { name -> nameResult = name },
                onEmailChanged = { email -> emailResult = email },
                onAgeChanged = { age -> ageResult = age },
                onAddUser = {},
                onClearError = {}
            )
        }

        // Test with Unicode characters (accented letters)
        composeTestRule.onNodeWithTag("NameInput").performTextInput("José María")
        composeTestRule.onNodeWithTag("EmailInput").performTextInput("jose@example.com")
        composeTestRule.onNodeWithTag("AgeInput").performTextInput("30")
        
        composeTestRule.onNodeWithTag("SaveButton").performClick()

        // Should accept Unicode letters and successfully create the user
        assert(nameResult == "José María")
        assert(emailResult == "jose@example.com")
        assert(ageResult == 30)
    }

    @Test
    fun addUserContent_showsError_whenEmailIsInvalid() {
        composeTestRule.setContent {
            AddUserContent(
                name = "John",
                email = "invalid-email",
                age = "25",
                isLoading = false,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("SaveButton").performClick()
        
        composeTestRule.onNodeWithText("Invalid email format").assertIsDisplayed()
    }

    @Test
    fun addUserContent_showsError_whenAgeIsInvalid() {
        composeTestRule.setContent {
            AddUserContent(
                name = "John",
                email = "john@example.com",
                age = "150",
                isLoading = false,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("SaveButton").performClick()
        composeTestRule.onNodeWithText("Enter a valid age (1-100)").assertIsDisplayed()

        // Test empty/zero age (if applicable)
        composeTestRule.onNodeWithTag("AgeInput").performTextClearance()
        composeTestRule.onNodeWithTag("AgeInput").performTextInput("0")
        composeTestRule.onNodeWithTag("SaveButton").performClick()
        composeTestRule.onNodeWithText("Enter a valid age (1-100)").assertIsDisplayed()
    }

    @Test
    fun addUserContent_displaysDuplicateEmailError() {
        val duplicateEmailMsg = "Email address is already in use."
        
        composeTestRule.setContent {
            AddUserContent(
                name = "John",
                email = "john@example.com",
                age = "25",
                isLoading = false,
                error = com.user.app.core.error.AppError.ValidationError(duplicateEmailMsg),
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("GeneralError").assertIsDisplayed()
        composeTestRule.onNodeWithText(duplicateEmailMsg).assertIsDisplayed()
    }

    @Test
    fun addUserContent_callsOnAddUser_whenInputsAreValid() {
        var addUserCalled = false

        composeTestRule.setContent {
            AddUserContent(
                name = "",
                email = "",
                age = "",
                isLoading = false,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = { addUserCalled = true },
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("NameInput").performTextInput("Alice")
        composeTestRule.onNodeWithTag("EmailInput").performTextInput("alice@test.com")
        composeTestRule.onNodeWithTag("AgeInput").performTextInput("25")
        
        composeTestRule.onNodeWithTag("SaveButton").performClick()

        assert(addUserCalled)
    }

    @Test
    fun addUserContent_disablesFields_whenSaving() {
        composeTestRule.setContent {
            AddUserContent(
                name = "John",
                email = "john@example.com",
                age = "25",
                isLoading = true,
                onNameChanged = {},
                onEmailChanged = {},
                onAgeChanged = {},
                onAddUser = {},
                onClearError = {}
            )
        }

        composeTestRule.onNodeWithTag("NameInput").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("EmailInput").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("AgeInput").assertIsNotEnabled()
        
        composeTestRule.onNodeWithTag("SaveButton").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("ProgressIndicator").assertIsDisplayed()
    }
}
