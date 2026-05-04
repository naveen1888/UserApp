package com.user.app.data.repository

import com.user.app.data.local.dao.UserDao
import com.user.app.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository pattern implementation for user data access.
 * 
 * Acts as an abstraction layer between the UI and data access layers.
 * Delegates all operations to UserDao while maintaining a clean API.
 * 
 * @param userDao The data access object for user operations
 */
open class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    
    /**
     * Retrieves all users from the database.
     * 
     * @return Flow emitting the list of all users
     */
    open fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    /**
     * Retrieves a specific user by ID.
     * 
     * @param id The user ID to search for
     * @return Flow emitting the user or null if not found
     */
    open fun getUserById(id: Int): Flow<User?> = userDao.getUserById(id)

    /**
     * Retrieves a user by email address.
     * 
     * @param email The email to search for
     * @return The user with the given email, or null if not found
     */
    open suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    /**
     * Inserts a new user into the database.
     * 
     * @param user The user to insert
     * @throws Exception if email already exists (unique constraint violation)
     */
    open suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}
