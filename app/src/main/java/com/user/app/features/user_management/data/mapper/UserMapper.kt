package com.user.app.features.user_management.data.mapper

import com.user.app.core.data.local.entity.User as UserEntity
import com.user.app.features.user_management.domain.model.User

/**
 * Mapper functions to convert between domain and data layer models
 */

/**
 * Convert database entity to domain model
 */
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        age = age
    )
}

/**
 * Convert domain model to database entity
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        age = age
    )
}
