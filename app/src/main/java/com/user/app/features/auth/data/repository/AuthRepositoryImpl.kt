package com.user.app.features.auth.data.repository

import com.user.app.features.auth.data.datasource.AuthRemoteDataSource
import com.user.app.features.auth.data.mapper.toDomain
import com.user.app.features.auth.domain.model.LoginResult
import com.user.app.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] that uses [AuthRemoteDataSource]for login
 * Handles mapping and error handling
 */
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun login(username: String, password: String): Flow<Result<LoginResult>> = flow {
        try {
            Timber.d("Attempting login for user: $username")
            val result = remoteDataSource.login(username, password)

            result.onSuccess { responseDto ->
                Timber.i("Login successful for user: $username")
                emit(Result.success(responseDto.toDomain()))
            }.onFailure { exception ->
                Timber.e(exception, "Login failed for user: $username")
                emit(Result.failure(exception))
            }
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during login")
            emit(Result.failure(e))
        }
    }
}

