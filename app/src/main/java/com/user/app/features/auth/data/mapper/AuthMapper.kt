package com.user.app.features.auth.data.mapper

import com.user.app.features.auth.domain.model.LoginResult
import com.user.app.features.auth.data.datasource.LoginResponseDto

/**
 * Mapper for converting login response from data layer to domain layer
 */
fun LoginResponseDto.toDomain(): LoginResult {
    return LoginResult(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        token = accessToken
    )
}

