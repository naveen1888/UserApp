package com.user.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.user.app.data.local.db.UserDatabase
import com.user.app.data.local.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var db: UserDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, UserDatabase::class.java
        ).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val user = User(id = 1, name = "Test User", email = "test@example.com", age = 30)
        userDao.insertUser(user)
        val allUsers = userDao.getAllUsers().first()
        assertEquals(allUsers[0].name, user.name)
    }

    @Test
    @Throws(Exception::class)
    fun getUserById() = runBlocking {
        val user = User(id = 1, name = "Test User", email = "test@example.com", age = 30)
        userDao.insertUser(user)
        
        val foundUser = userDao.getUserById(1).first()
        assertEquals(user, foundUser)
        
        val notFoundUser = userDao.getUserById(2).first()
        assertNull(notFoundUser)
    }

    @Test
    fun getUserByEmail() = runBlocking {
        val email = "unique@test.com"
        val user = User(id = 1, name = "Test User", email = email, age = 30)
        userDao.insertUser(user)
        
        val foundUser = userDao.getUserByEmail(email)
        assertEquals(user, foundUser)
        
        val notFoundUser = userDao.getUserByEmail("other@test.com")
        assertNull(notFoundUser)
    }

    @Test(expected = android.database.sqlite.SQLiteConstraintException::class)
    fun insertDuplicateEmail_throwsException() = runBlocking {
        val user1 = User(id = 1, name = "User 1", email = "dup@test.com", age = 20)
        val user2 = User(id = 2, name = "User 2", email = "dup@test.com", age = 30)
        
        userDao.insertUser(user1)
        userDao.insertUser(user2) // This should throw SQLiteConstraintException due to unique index
    }

    @Test
    fun insertMultipleUsers_orderedByName() = runBlocking {
        val user1 = User(id = 1, name = "Charlie", email = "c@test.com", age = 20)
        val user2 = User(id = 2, name = "Alice", email = "a@test.com", age = 22)
        val user3 = User(id = 3, name = "Bob", email = "b@test.com", age = 21)
        
        userDao.insertUser(user1)
        userDao.insertUser(user2)
        userDao.insertUser(user3)
        
        val allUsers = userDao.getAllUsers().first()
        assertEquals(3, allUsers.size)
        assertEquals("Alice", allUsers[0].name)
        assertEquals("Bob", allUsers[1].name)
        assertEquals("Charlie", allUsers[2].name)
    }
}
