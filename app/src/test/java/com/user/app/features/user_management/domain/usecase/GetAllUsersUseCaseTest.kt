package com.user.app.features.user_management.domain.usecase

import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetAllUsersUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAllUsersUseCase = GetAllUsersUseCase(userRepository)
    }

    @Test
    fun `invoke returns users from repository`() = runTest {
        val users = listOf(
            User(1, "John Doe", "john@example.com", 30),
            User(2, "Jane Smith", "jane@example.com", 25)
        )

        whenever(userRepository.getAllUsers()).thenReturn(flowOf(users))

        val result = getAllUsersUseCase().first()

        assertEquals(users, result)
    }

    @Test
    fun `invoke returns empty list when no users exist`() = runTest {
        val users = emptyList<User>()

        whenever(userRepository.getAllUsers()).thenReturn(flowOf(users))

        val result = getAllUsersUseCase().first()

        assertEquals(users, result)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        val users = listOf(User(1, "Test User", "test@example.com", 20))

        whenever(userRepository.getAllUsers()).thenReturn(flowOf(users))

        val flow = getAllUsersUseCase()
        val result = flow.first()

        assertEquals(users, result)
    }
}
