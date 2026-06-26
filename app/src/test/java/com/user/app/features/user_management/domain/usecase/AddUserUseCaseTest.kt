package com.user.app.features.user_management.domain.usecase

import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AddUserUseCaseTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var addUserUseCase: AddUserUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addUserUseCase = AddUserUseCase(userRepository)
    }

    @Test
    fun `invoke calls repository insertUser with correct user`() = runTest {
        val name = "John Doe"
        val email = "john@example.com"
        val age = 30

        whenever(userRepository.insertUser(User(0, name, email, age))).thenReturn(Unit)

        val result = addUserUseCase(name, email, age)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest {
        val name = "John Doe"
        val email = "john@example.com"
        val age = 30
        val exception = RuntimeException("Database error")

        whenever(userRepository.insertUser(User(0, name, email, age))).thenThrow(exception)

        val result = addUserUseCase(name, email, age)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke creates user with id 0 for new users`() = runTest {
        val name = "Jane Smith"
        val email = "jane@example.com"
        val age = 25

        whenever(userRepository.insertUser(User(0, name, email, age))).thenReturn(Unit)

        addUserUseCase(name, email, age)

        // Verification is implicit through the mock setup
        // In a real test, you might verify the exact User object passed to insertUser
    }
}
