package com.user.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.user.app.data.local.dao.UserDao
import com.user.app.data.local.entity.User

/**
 * Room database for user management.
 * 
 * Provides access to UserDao for database operations.
 * Hilt manages the singleton instance of this database.
 */
@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
