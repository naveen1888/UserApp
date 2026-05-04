package com.user.app.ui.adduser

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class AddUserScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addUserContent_displaysInputFields() {
        composeTestRule.setContent {
            AddUserContent(isSaving = false, onAddUser = { _, _, _ -> })
        }

        composeTestRule.onNodeWithTag("NameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EmailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AgeInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SaveButton").assertIsDisplayed()
    }

    @Test
    fun addUserContent_showsErrors_whenInputsAreEmpty() {
        composeTestRule.setContent {
            AddUserContent(isSaving = false, onAddUser = { _, _, _ -> })
        }

        composeTestRule.onNodeWithTag("SaveButton").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Age cannot be empty").assertIsDisplayed()
    }

    @Test
    fun addUserContent_showsError_whenNameIsInvalid() {
        composeTestRule.setContent {
            AddUserContent(isSaving = false, onAddUser = { _, _, _ -> })
        }

        // Names with numbers should be invalid
        composeTestRule.onNodeWithTag("NameInput").performTextInput("John123")
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
                isSaving = false,
                onAddUser = { name, email, age ->
                    nameResult = name
                    emailResult = email
                    ageResult = age
                }
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
            AddUserContent(isSaving = false, onAddUser = { _, _, _ -> })
        }

        composeTestRule.onNodeWithTag("EmailInput").performTextInput("invalid-email")
        composeTestRule.onNodeWithTag("SaveButton").performClick()
        
        composeTestRule.onNodeWithText("Invalid email format").assertIsDisplayed()
    }

    @Test
    fun addUserContent_showsError_whenAgeIsInvalid() {
        composeTestRule.setContent {
            AddUserContent(isSaving = false, onAddUser = { _, _, _ -> })
        }

        // Test age above 100
        composeTestRule.onNodeWithTag("AgeInput").performTextInput("150")
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
                isSaving = false,
                errorMessage = duplicateEmailMsg,
                onAddUser = { _, _, _ -> }
            )
        }

        composeTestRule.onNodeWithTag("GeneralError").assertIsDisplayed()
        composeTestRule.onNodeWithText(duplicateEmailMsg).assertIsDisplayed()
    }

    @Test
    fun addUserContent_callsOnAddUser_whenInputsAreValid() {
        var nameResult = ""
        var emailResult = ""
        var ageResult = 0

        composeTestRule.setContent {
            AddUserContent(
                isSaving = false,
                onAddUser = { name, email, age ->
                    nameResult = name
                    emailResult = email
                    ageResult = age
                }
            )
        }

        composeTestRule.onNodeWithTag("NameInput").performTextInput("Alice")
        composeTestRule.onNodeWithTag("EmailInput").performTextInput("alice@test.com")
        composeTestRule.onNodeWithTag("AgeInput").performTextInput("25")
        
        composeTestRule.onNodeWithTag("SaveButton").performClick()

        assert(nameResult == "Alice")
        assert(emailResult == "alice@test.com")
        assert(ageResult == 25)
    }

    @Test
    fun addUserContent_disablesFields_whenSaving() {
        composeTestRule.setContent {
            AddUserContent(isSaving = true, onAddUser = { _, _, _ -> })
        }

        composeTestRule.onNodeWithTag("NameInput").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("EmailInput").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("AgeInput").assertIsNotEnabled()
        
        composeTestRule.onNodeWithTag("SaveButton").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("ProgressIndicator").assertIsDisplayed()
    }
}
