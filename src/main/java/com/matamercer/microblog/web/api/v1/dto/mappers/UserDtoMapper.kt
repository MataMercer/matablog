package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.web.api.v1.dto.responses.UserResponseDto

fun User.toUserResponseDto(): UserResponseDto {
    val dto = toBaseModelResponseDto(UserResponseDto())
    return dto
}