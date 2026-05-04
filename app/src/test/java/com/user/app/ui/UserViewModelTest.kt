package com.user.app.ui

import com.user.app.data.local.entity.User
import com.user.app.data.remote.model.LoginRequest
import com.user.app.data.remote.model.LoginResponse
import com.user.app.data.repository.AuthRepository
import com.user.app.data.repository.UserRepository
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
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: UserViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        `when`(userRepository.getAllUsers()).thenReturn(flowOf(emptyList()))
        viewModel = UserViewModel(userRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun allUsers_initialValueIsEmpty() = runTest {
        val users = viewModel.allUsers.first()
        assertTrue(users.isEmpty())
    }

    @Test
    fun allUsers_updatesWhenRepositoryEmits() = runTest {
        val users = listOf(User(1, "John", "john@test.com", 25))
        `when`(userRepository.getAllUsers()).thenReturn(flowOf(users))
        
        // Create new VM to pick up the new flow from repository
        val newViewModel = UserViewModel(userRepository, authRepository)
        
        // StateFlow initially emits the initialValue (emptyList). 
        // We use first { it.isNotEmpty() } to wait for the mocked emission from the repository.
        val result = newViewModel.allUsers.first { it.isNotEmpty() }
        assertEquals(users, result)
    }

    @Test
    fun getUserById_callsRepository() = runTest {
        val user = User(1, "John", "john@test.com", 25)
        `when`(userRepository.getUserById(1)).thenReturn(flowOf(user))
        
        val result = viewModel.getUserById(1).first()
        
        assertEquals(user, result)
        verify(userRepository).getUserById(1)
    }

    @Test
    fun addUser_callsRepositoryAndTriggersOnComplete_whenEmailIsUnique() = runTest {
        val name = "John"
        val email = "john@test.com"
        val age = 25
        var completed = false
        
        whenever(userRepository.getUserByEmail(email)).thenReturn(null)
        
        viewModel.addUser(name, email, age) {
            completed = true
        }
        
        advanceUntilIdle()
        
        verify(userRepository).insertUser(User(name = name, email = email, age = age))
        assertTrue(completed)
        assertFalse(viewModel.isSaving.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun addUser_setsError_whenEmailAlreadyExists() = runTest {
        val email = "existing@test.com"
        val existingUser = User(1, "Old User", email, 30)
        
        whenever(userRepository.getUserByEmail(email)).thenReturn(existingUser)
        
        viewModel.addUser("New User", email, 25) {}
        
        advanceUntilIdle()
        
        verify(userRepository, never()).insertUser(any())
        assertEquals(UserError.DuplicateEmail, viewModel.error.value)
        assertFalse(viewModel.isSaving.value)
    }

    @Test
    fun addUser_setsIsSavingCorrectly() = runTest {
        whenever(userRepository.getUserByEmail(any())).thenReturn(null)
        
        viewModel.addUser("John", "john@test.com", 25) {}
        
        // isSaving should be true immediately after call (before dispatcher runs)
        assertTrue(viewModel.isSaving.value)
        
        advanceUntilIdle()
        
        // isSaving should be false after coroutine finishes
        assertFalse(viewModel.isSaving.value)
    }

    @Test
    fun addUser_preventsConcurrentCalls() = runTest {
        whenever(userRepository.getUserByEmail(any())).thenReturn(null)
        
        viewModel.addUser("User1", "u1@test.com", 20) {}
        viewModel.addUser("User2", "u2@test.com", 30) {}
        
        advanceUntilIdle()
        
        // Should only have called insert once
        verify(userRepository, times(1)).insertUser(any())
    }

    @Test
    fun addUser_handlesErrorAndResetsIsSaving() = runTest {
        whenever(userRepository.getUserByEmail(any())).thenReturn(null)
        whenever(userRepository.insertUser(any())).thenThrow(RuntimeException("DB Error"))
        
        viewModel.addUser("John", "john@test.com", 25) {}
        
        advanceUntilIdle()
        
        assertFalse(viewModel.isSaving.value)
        assertEquals(UserError.DatabaseError, viewModel.error.value)
    }

    @Test
    fun login_callsAuthRepositoryAndTriggersOnComplete_onSuccess() = runTest {
        val request = LoginRequest("test", "pass")
        val response = Response.success(
            LoginResponse(
                id = 1,
                username = "test",
                email = "test@test.com",
                firstName = "Test",
                lastName = "User",
                gender = "male",
                image = "",
                accessToken = "token",
                refreshToken = "refresh"
            )
        )
        var completedEmail: String? = null

        whenever(authRepository.login(request)).thenReturn(response)

        viewModel.login(request) { completedEmail = it }

        assertTrue(viewModel.isLoggingIn.value)
        advanceUntilIdle()

        verify(authRepository).login(request)
        assertEquals("test", completedEmail)
        assertFalse(viewModel.isLoggingIn.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun login_setsError_onFailure() = runTest {
        val request = LoginRequest("test", "pass")
        val response = Response.success<LoginResponse>(null)

        whenever(authRepository.login(request)).thenReturn(response)

        viewModel.login(request) {}

        advanceUntilIdle()

        verify(authRepository).login(request)
        assertTrue(viewModel.error.value is UserError.UnknownError)
        assertFalse(viewModel.isLoggingIn.value)
    }

    @Test
    fun login_setsNetworkError_onException() = runTest {
        val request = LoginRequest("test", "pass")

        whenever(authRepository.login(request)).thenThrow(RuntimeException("Network failure"))

        viewModel.login(request) {}

        advanceUntilIdle()

        assertEquals(UserError.NetworkError, viewModel.error.value)
        assertFalse(viewModel.isLoggingIn.value)
    }

    @Test
    fun clearError_resetsErrorState() = runTest {
        whenever(userRepository.getUserByEmail(any())).thenReturn(User(1, "Existing", "e@t.com", 20))
        
        viewModel.addUser("New", "e@t.com", 25) {}
        advanceUntilIdle()
        assertNotNull(viewModel.error.value)
        
        viewModel.clearError()
        assertNull(viewModel.error.value)
    }

    @Test
    fun stopAllOperations_cancelsJobAndResetsState() = runTest {
        whenever(userRepository.getUserByEmail(any())).thenReturn(null)
        viewModel.addUser("John", "john@test.com", 25) {}
        assertTrue(viewModel.isSaving.value)
        
        viewModel.stopAllOperations()
        
        assertFalse(viewModel.isSaving.value)
        assertNull(viewModel.error.value)
        
        advanceUntilIdle()
    }
}
