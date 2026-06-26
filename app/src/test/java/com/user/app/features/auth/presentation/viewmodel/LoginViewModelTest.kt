package com.user.app.features.auth.presentation.viewmodel

import com.user.app.core.domain.usecase.SaveLoginStatusUseCase
import com.user.app.features.auth.domain.model.LoginResult
import com.user.app.features.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @Mock
    private lateinit var loginUseCase: LoginUseCase

    @Mock
    private lateinit var saveLoginStatusUseCase: SaveLoginStatusUseCase

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase, saveLoginStatusUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertNull(state.loginResult)
        assertFalse(state.isLoggedIn)
        assertEquals("", state.username)
        assertEquals("", state.password)
    }

    @Test
    fun `login success saves session and updates state`() = runTest {
        val loginResult = LoginResult(
            id = 1,
            username = "testuser",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            token = "token123"
        )
        whenever(loginUseCase(any(), any())).thenReturn(flowOf(Result.success(loginResult)))

        viewModel.onUsernameChanged("testuser")
        viewModel.onPasswordChanged("password")
        viewModel.login()

        advanceUntilIdle()

        // Verify session was saved via UseCase
        verify(saveLoginStatusUseCase).invoke(true, "test@example.com")
        
        val state = viewModel.state.value
        assertTrue(state.isLoggedIn)
        assertEquals(loginResult, state.loginResult)
        assertFalse(state.isLoading)
    }

    @Test
    fun `login failure updates state with error`() = runTest {
        whenever(loginUseCase(any(), any())).thenReturn(flowOf(Result.failure(RuntimeException("401"))))

        viewModel.onUsernameChanged("user")
        viewModel.onPasswordChanged("wrong")
        viewModel.login()

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoggedIn)
        assertNotNull(state.error)
    }
}
