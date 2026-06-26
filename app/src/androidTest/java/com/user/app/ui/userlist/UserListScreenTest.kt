package com.user.app.ui.userlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.presentation.components.UserItem
import com.user.app.features.user_management.presentation.screen.UserListContent
import org.junit.Rule
import org.junit.Test

class UserListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userItem_displaysUserInformation() {
        val user = User(id = 1, name = "John Doe", email = "john@example.com", age = 30)
        
        composeTestRule.setContent {
            UserItem(user = user, onClick = {})
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("john@example.com").assertIsDisplayed()
    }

    @Test
    fun userListContent_displaysListOfUsers() {
        val users = listOf(
            User(1, "John Doe", "john@example.com", 30),
            User(2, "Jane Smith", "jane@example.com", 25)
        )

        composeTestRule.setContent {
            UserListContent(
                users = users,
                onUserClick = {},
                onAddUserClick = {}
            )
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
    }

    @Test
    fun userListContent_fabClick_triggersOnAddUserClick() {
        var fabClicked = false

        composeTestRule.setContent {
            UserListContent(
                users = emptyList(),
                onUserClick = {},
                onAddUserClick = { fabClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Add User").performClick()
        assert(fabClicked)
    }
}
