package com.user.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.user.app.data.local.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User entity operations.
 * 
 * Provides methods to perform CRUD operations on the users table.
 * All query methods return Flow for reactive updates.
 */
@Dao
interface UserDao {
    
    /**
     * Retrieves all users from the database, ordered by name.
     * 
     * @return Flow emitting the list of users ordered alphabetically
     */
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    /**
     * Retrieves a specific user by ID.
     * 
     * @param id The user ID to search for
     * @return Flow emitting the user or null if not found
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flow<User?>

    /**
     * Retrieves a user by email address.
     * 
     * @param email The email to search for
     * @return The user with the given email, or null if not found
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Inserts a new user into the database.
     * 
     * @param user The user to insert
     * @throws Exception if email already exists (unique constraint violation)
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)
}
