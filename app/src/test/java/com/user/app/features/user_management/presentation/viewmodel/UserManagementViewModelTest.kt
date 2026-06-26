package com.user.app.features.user_management.presentation.viewmodel

import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.domain.usecase.AddUserUseCase
import com.user.app.features.user_management.domain.usecase.GetAllUsersUseCase
import com.user.app.features.user_management.domain.usecase.GetUserByIdUseCase
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserManagementViewModelTest {

    @Mock
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    @Mock
    private lateinit var getUserByIdUseCase: GetUserByIdUseCase

    @Mock
    private lateinit var addUserUseCase: AddUserUseCase

    private lateinit var viewModel: UserManagementViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        val users = listOf(
            User(1, "John Doe", "john@example.com", 30),
            User(2, "Jane Smith", "jane@example.com", 25)
        )
        whenever(getAllUsersUseCase()).thenReturn(flowOf(users))

        viewModel = UserManagementViewModel(
            getAllUsersUseCase = getAllUsersUseCase,
            getUserByIdUseCase = getUserByIdUseCase,
            addUserUseCase = addUserUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads users correctly`() = runTest {
        advanceUntilIdle()

        val state = viewModel.userListState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.users.size)
        assertEquals("Jane Smith", state.users[0].name) // sorted by name
        assertEquals("John Doe", state.users[1].name)
    }

    @Test
    fun `onNameChanged updates name and validates`() = runTest {
        viewModel.onNameChanged("Valid Name")
        val state = viewModel.addUserState.first()
        assertEquals("Valid Name", state.name)
        assertNull(state.nameError)
    }

    @Test
    fun `onNameChanged shows error for invalid name`() = runTest {
        viewModel.onNameChanged("John123")
        val state = viewModel.addUserState.first()
        assertEquals("John123", state.name)
        assertNotNull(state.nameError)
        assertEquals("Name can only contain letters and spaces", state.nameError)
    }

    @Test
    fun `onEmailChanged updates email and validates`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        val state = viewModel.addUserState.first()
        assertEquals("test@example.com", state.email)
        assertNull(state.emailError)
    }

    @Test
    fun `onEmailChanged shows error for invalid email`() = runTest {
        viewModel.onEmailChanged("invalid-email")
        val state = viewModel.addUserState.first()
        assertEquals("invalid-email", state.email)
        assertNotNull(state.emailError)
        assertEquals("Invalid email format", state.emailError)
    }

    @Test
    fun `onAgeChanged updates age and validates`() = runTest {
        viewModel.onAgeChanged("25")
        val state = viewModel.addUserState.first()
        assertEquals("25", state.age)
        assertNull(state.ageError)
    }

    @Test
    fun `onAgeChanged shows error for invalid age`() = runTest {
        viewModel.onAgeChanged("150")
        val state = viewModel.addUserState.first()
        assertEquals("150", state.age)
        assertNotNull(state.ageError)
        assertEquals("Enter a valid age (1-100)", state.ageError)
    }

    @Test
    fun `addUser success updates state correctly`() = runTest {
        whenever(addUserUseCase("John", "john@test.com", 25)).thenReturn(Result.success(Unit))

        viewModel.onNameChanged("John")
        viewModel.onEmailChanged("john@test.com")
        viewModel.onAgeChanged("25")
        viewModel.addUser()

        advanceUntilIdle()

        val state = viewModel.addUserState.first()
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.isSuccess)
    }

    @Test
    fun `addUser failure shows error`() = runTest {
        whenever(addUserUseCase("John", "existing@test.com", 25)).thenReturn(Result.failure(RuntimeException("Duplicate email")))

        viewModel.onNameChanged("John")
        viewModel.onEmailChanged("existing@test.com")
        viewModel.onAgeChanged("25")
        viewModel.addUser()

        advanceUntilIdle()

        val state = viewModel.addUserState.first()
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertFalse(state.isSuccess)
    }

    @Test
    fun `getUserDetails loads user correctly`() = runTest {
        val user = User(1, "John Doe", "john@example.com", 30)
        whenever(getUserByIdUseCase(1)).thenReturn(flowOf(user))

        val userStateFlow = viewModel.getUserDetails(1)
        advanceUntilIdle()

        val userState = userStateFlow.first()
        assertFalse(userState.isLoading)
        assertNull(userState.error)
        assertEquals(user, userState.user)
    }

    @Test
    fun `resetAddUserForm clears form state`() = runTest {
        viewModel.onNameChanged("John")
        viewModel.onEmailChanged("john@test.com")
        viewModel.onAgeChanged("25")

        viewModel.resetAddUserForm()

        val state = viewModel.addUserState.first()
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.age)
        assertNull(state.nameError)
        assertNull(state.emailError)
        assertNull(state.ageError)
        assertFalse(state.isSuccess)
    }

    @Test
    fun `addUser sets loading state during operation`() = runTest {
        whenever(addUserUseCase("John", "john@test.com", 25)).thenReturn(Result.success(Unit))

        viewModel.onNameChanged("John")
        viewModel.onEmailChanged("john@test.com")
        viewModel.onAgeChanged("25")
        viewModel.addUser()

        val loadingState = viewModel.addUserState.first()
        assertTrue(loadingState.isLoading)

        advanceUntilIdle()

        val finalState = viewModel.addUserState.first()
        assertFalse(finalState.isLoading)
    }
}
