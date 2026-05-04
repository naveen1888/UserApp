package com.user.app.data.repository

import com.user.app.data.local.dao.UserDao
import com.user.app.data.local.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class UserRepositoryTest {

    @Mock
    private lateinit var userDao: UserDao

    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = UserRepository(userDao)
    }

    @Test
    fun getAllUsers_returnsUsersFromDao() = runTest {
        val users = listOf(User(1, "John", "john@test.com", 25))
        `when`(userDao.getAllUsers()).thenReturn(flowOf(users))

        val result = repository.getAllUsers().first()

        assertEquals(users, result)
    }

    @Test
    fun getUserById_returnsUserFromDao() = runTest {
        val user = User(1, "John", "john@test.com", 25)
        `when`(userDao.getUserById(1)).thenReturn(flowOf(user))

        val result = repository.getUserById(1).first()

        assertEquals(user, result)
    }

    @Test
    fun getUserByEmail_returnsUserFromDao() = runTest {
        val user = User(1, "John", "john@test.com", 25)
        `when`(userDao.getUserByEmail("john@test.com")).thenReturn(user)

        val result = repository.getUserByEmail("john@test.com")

        assertEquals(user, result)
    }

    @Test
    fun insertUser_callsDaoInsert() = runTest {
        val user = User(1, "John", "john@test.com", 25)
        
        repository.insertUser(user)
        
        verify(userDao).insertUser(user)
    }
}
