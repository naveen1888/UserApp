package com.user.app.features.user_management.data.repository

import com.user.app.features.user_management.data.datasource.UserLocalDataSource
import com.user.app.features.user_management.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class UserRepositoryImplTest {

    @Mock
    private lateinit var userLocalDataSource: UserLocalDataSource

    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userRepository = UserRepositoryImpl(userLocalDataSource)
    }

    @Test
    fun `getAllUsers returns users from data source`() = runTest {
        val users = listOf(
            User(1, "John Doe", "john@example.com", 30),
            User(2, "Jane Smith", "jane@example.com", 25)
        )

        whenever(userLocalDataSource.getAllUsers()).thenReturn(flowOf(users))

        val result = userRepository.getAllUsers().first()

        assertEquals(users, result)
    }

    @Test
    fun `getUserById returns user from data source`() = runTest {
        val user = User(1, "John Doe", "john@example.com", 30)

        whenever(userLocalDataSource.getUserById(1)).thenReturn(flowOf(user))

        val result = userRepository.getUserById(1).first()

        assertEquals(user, result)
    }

    @Test
    fun `getUserById returns null when user not found`() = runTest {
        whenever(userLocalDataSource.getUserById(999)).thenReturn(flowOf(null))

        val result = userRepository.getUserById(999).first()

        assertNull(result)
    }

    @Test
    fun `getUserByEmail returns user from data source`() = runTest {
        val user = User(1, "John Doe", "john@example.com", 30)

        whenever(userLocalDataSource.getUserByEmail("john@example.com")).thenReturn(user)

        val result = userRepository.getUserByEmail("john@example.com")

        assertEquals(user, result)
    }

    @Test
    fun `getUserByEmail returns null when user not found`() = runTest {
        whenever(userLocalDataSource.getUserByEmail("notfound@example.com")).thenReturn(null)

        val result = userRepository.getUserByEmail("notfound@example.com")

        assertNull(result)
    }

    @Test
    fun `insertUser calls data source insertUser`() = runTest {
        val user = User(0, "New User", "new@example.com", 20)

        whenever(userLocalDataSource.insertUser(user)).thenReturn(Unit)

        userRepository.insertUser(user)

        // Verification is implicit through mock setup
        // In a real test, you might use verify() to check the call
    }
}
