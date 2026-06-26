package com.user.app.features.user_management.domain.usecase

import com.user.app.features.user_management.domain.model.User
import com.user.app.features.user_management.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all users
 */
class GetAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = repository.getAllUsers()
}

/**
 * Use case to retrieve a specific user by ID
 */
class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(id: Int): Flow<User?> = repository.getUserById(id)
}

/**
 * Use case to add a new user
 * Encapsulates validation logic and duplicate email checks
 */
class AddUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String, age: Int): Result<Unit> {
        return try {
            val existingUser = repository.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception("DuplicateEmail"))
            } else {
                repository.insertUser(User(name = name, email = email, age = age))
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

