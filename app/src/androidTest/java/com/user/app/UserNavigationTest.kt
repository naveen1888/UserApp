package com.user.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import com.user.app.core.domain.repository.UserPreferencesRepository
import com.user.app.core.domain.usecase.GetLoginStatusUseCase
import com.user.app.core.domain.usecase.SaveLoginStatusUseCase
import com.user.app.features.auth.domain.repository.AuthRepository
import com.user.app.features.auth.domain.usecase.LoginUseCase
import com.user.app.features.auth.presentation.viewmodel.LoginViewModel
import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.domain.repository.UserRepository
import com.user.app.features.user_management.domain.usecase.AddUserUseCase
import com.user.app.features.user_management.domain.usecase.GetAllUsersUseCase
import com.user.app.features.user_management.domain.usecase.GetUserByIdUseCase
import com.user.app.features.user_management.presentation.viewmodel.UserManagementViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

class UserNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userManagementViewModel: UserManagementViewModel
    private lateinit var getLoginStatusUseCase: GetLoginStatusUseCase

    private val usersFlow = MutableStateFlow<List<com.user.app.features.user_management.domain.model.User>>(emptyList())

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        val loginUseCase = LoginUseCase(authRepository)
        val saveLoginStatusUseCase = SaveLoginStatusUseCase(userPreferencesRepository)
        loginViewModel = LoginViewModel(loginUseCase, saveLoginStatusUseCase)
        
        getLoginStatusUseCase = GetLoginStatusUseCase(userPreferencesRepository)
        
        userManagementViewModel = UserManagementViewModel(
            getAllUsersUseCase = GetAllUsersUseCase(userRepository),
            getUserByIdUseCase = GetUserByIdUseCase(userRepository),
            addUserUseCase = AddUserUseCase(userRepository)
        )

        whenever(userRepository.getAllUsers()).thenReturn(usersFlow)
        whenever(userPreferencesRepository.isLoggedIn).thenReturn(flowOf(false))
    }

    @Test
    fun testFullAppFlow(): Unit = runBlocking {
        // Mocking insertUser to update the flow so the UI reflects the change
        whenever(userRepository.insertUser(any())).doAnswer { invocation ->
            val user = invocation.getArgument<User>(0)
            usersFlow.value += user
            null
        }

        composeTestRule.setContent {
            UserApp(
                getLoginStatusUseCase = getLoginStatusUseCase,
                loginViewModel = loginViewModel,
                userViewModel = userManagementViewModel
            )
        }

        // Verify initial state
        composeTestRule.onAllNodesWithText("Login")[0].assertIsDisplayed()
    }
}
