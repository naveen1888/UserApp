package com.user.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import com.user.app.data.local.entity.User
import com.user.app.data.local.pref.UserPreferencesRepository
import com.user.app.data.remote.model.LoginResponse
import com.user.app.data.repository.AuthRepository
import com.user.app.data.repository.UserRepository
import com.user.app.ui.UserViewModel
import com.user.app.ui.UserViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import retrofit2.Response

class UserNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var repository: UserRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(repository.getAllUsers()).thenReturn(usersFlow)
        `when`(userPreferencesRepository.isLoggedIn).thenReturn(flowOf(false))
    }

    @Test
    fun testFullAppFlow() {
        val factory = UserViewModelFactory(repository, authRepository)
        
        runBlocking {
            // Mock successful login response
            val loginResponse = Response.success(
                LoginResponse(
                    id = 1,
                    username = "testuser",
                    email = "test@example.com",
                    firstName = "Test",
                    lastName = "User",
                    gender = "male",
                    image = "url",
                    accessToken = "access",
                    refreshToken = "refresh"
                )
            )
            whenever(authRepository.login(any())).thenReturn(loginResponse)

            // Mocking insertUser to update the flow so the UI reflects the change
            doAnswer { invocation: InvocationOnMock ->
                val user = invocation.getArgument<User>(0)
                usersFlow.value += user
                null
            }.whenever(repository).insertUser(any())
            
            doReturn(null).whenever(repository).getUserByEmail(any())
        }

        composeTestRule.setContent {
            val viewModel: UserViewModel = viewModel(factory = factory)
            UserApp(
                userPreferencesRepository = userPreferencesRepository,
                viewModel = viewModel
            )
        }

        // 1. Start on Login screen
        // Use onAllNodesWithText because "Login" appears in both the title and the button
        composeTestRule.onAllNodesWithText("Login")[0].assertIsDisplayed()
        
        composeTestRule.onNodeWithTag("UsernameInput").performTextInput("testuser")
        composeTestRule.onNodeWithTag("PasswordInput").performTextInput("password")

        composeTestRule.onNodeWithTag("LoginButton").performClick()

        // 2. Verify we are now on User List screen
        composeTestRule.onNodeWithText("User List").assertIsDisplayed()

        // 3. Navigate to Add User
        composeTestRule.onNodeWithContentDescription("Add User").performClick()
        composeTestRule.onNodeWithText("Add User").assertIsDisplayed()

        // 4. Fill form and save
        composeTestRule.onNodeWithTag("NameInput").performTextInput("John Doe")
        composeTestRule.onNodeWithTag("EmailInput").performTextInput("john@example.com")
        composeTestRule.onNodeWithTag("AgeInput").performTextInput("30")
        
        composeTestRule.onNodeWithTag("SaveButton").performClick()

        // 5. Verify we are back on User List and the new user is visible
        composeTestRule.onNodeWithText("User List").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()

        // 6. Navigate to User Details
        `when`(repository.getUserById(any())).thenReturn(MutableStateFlow(User(1, "John Doe", "john@example.com", 30)))
        
        composeTestRule.onNodeWithText("John Doe").performClick()
        
        // 7. Verify User Details screen
        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name: John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email: john@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Age: 30").assertIsDisplayed()

        // 8. Go back to list
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("User List").assertIsDisplayed()
    }
}
