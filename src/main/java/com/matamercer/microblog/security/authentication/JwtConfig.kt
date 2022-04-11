package com.matamercer.microblog.security.authentication

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application.jwt")
class JwtConfig {
    var secretKey: String? = null
    var tokenPrefix: String = "Bearer"
    var refreshTokenExpirationInDays: Int = 0
    var accessTokenExpirationInHours: Int = 0
    val authorizationHeader: String
        get() = HttpHeaders.AUTHORIZATION
    val refreshTokenHeader: String
        get() = "refreshToken"
}