package com.user.app.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Data class representing a User entity in the database.
 * 
 * @property id Unique identifier for the user (auto-generated)
 * @property name User's full name
 * @property email User's unique email address
 * @property age User's age
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val age: Int
)
