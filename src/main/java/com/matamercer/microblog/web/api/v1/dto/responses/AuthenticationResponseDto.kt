package com.matamercer.microblog.web.api.v1.dto.responses

class AuthenticationResponseDto(
    val accessToken: String? = null,
    val refreshToken: String? = null
)