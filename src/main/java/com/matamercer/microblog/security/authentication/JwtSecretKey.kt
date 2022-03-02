package com.matamercer.microblog.security.authentication

import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
class JwtSecretKey @Autowired constructor(private val jwtConfig: JwtConfig) {
    @get:Bean
    val secretKey: SecretKey
        get() = Keys.hmacShaKeyFor(jwtConfig.secretKey!!.toByteArray())
}