package com.user.app.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.user.app.core.data.local.dao.UserDao
import com.user.app.core.data.local.entity.User

/**
 * Room database for user management.
 */
@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
