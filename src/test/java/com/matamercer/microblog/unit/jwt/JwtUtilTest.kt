package com.matamercer.microblog.unit.jwt

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.RefreshToken
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.RefreshTokenRepository
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authentication.JwtConfig
import com.matamercer.microblog.security.authentication.JwtSecretKey
import com.matamercer.microblog.security.authentication.JwtUtil
import com.matamercer.microblog.security.authorization.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@TestPropertySource("/application.properties")
@ActiveProfiles("test")
class JwtUtilTest {
    @Value("\${application.jwt.secretKey}")
    private val jwtConfigSecretKey: String? = null

    @Value("\${application.jwt.tokenPrefix}")
    private val jwtConfigTokenPrefix: String? = null

    @Value("\${application.jwt.refreshTokenExpirationInDays}")
    private val jwtConfigRefreshTokenExpirationInDays: Int? = null

    @Value("\${application.jwt.accessTokenExpirationInHours}")
    private val getJwtConfigAccessTokenExpirationInHours: Int? = null
    private var user: User? = null
    private var jwtUtil: JwtUtil? = null
    @Before
    fun setup() {
        user = User(
            "username@gmail.com",
            "username",
            "password",
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        )
        user!!.id = 0L
        val jwtConfig = JwtConfig()
        jwtConfig.secretKey = jwtConfigSecretKey
        jwtConfig.tokenPrefix = jwtConfigTokenPrefix
        if (jwtConfigRefreshTokenExpirationInDays != null) {
            jwtConfig.refreshTokenExpirationInDays = jwtConfigRefreshTokenExpirationInDays
        }
        if (getJwtConfigAccessTokenExpirationInHours != null) {
            jwtConfig.accessTokenExpirationInHours = getJwtConfigAccessTokenExpirationInHours
        }
        val jwtSecretKey = JwtSecretKey(jwtConfig)
        val secretKey = jwtSecretKey.secretKey
        val userRepository = Mockito.mock(UserRepository::class.java)
        Mockito.`when`(
            userRepository.findByUsername(
                user!!.username
            )
        )
            .thenReturn(Optional.ofNullable(user))
        Mockito.`when`(userRepository.findById(user!!.id))
            .thenReturn(Optional.ofNullable(user))
        val refreshToken = RefreshToken(user)
        refreshToken.id = 0L
        val refreshTokenRepository = Mockito.mock(
            RefreshTokenRepository::class.java
        )
        Mockito.`when`(
            refreshTokenRepository.save(
                ArgumentMatchers.any(
                    RefreshToken::class.java
                )
            )
        )
            .thenReturn(refreshToken)
        jwtUtil = JwtUtil(secretKey, jwtConfig, userRepository, refreshTokenRepository)
    }

    @Test
    fun whenCreateAccessToken_returnValidAccessToken() {
        val token = jwtUtil!!.createAccessToken(user!!.id!!)
        val claims = jwtUtil!!.extractAllClaims(token)
        assertThat(jwtUtil!!.isTokenExpired(claims)).isFalse
        assertThat(jwtUtil!!.getUserId(claims)).isEqualTo(user!!.id)
        assertThat(jwtUtil!!.getUserName(claims)).isEqualTo(user!!.username)
        assertThat(jwtUtil!!.getUserRole(claims)).isEqualTo(
            user!!.role
        )
    }

    @Test
    fun whenCreateRefreshToken_returnValidRefreshToken() {
        val token = jwtUtil!!.createRefreshToken(user!!.id!!)
        val claims = jwtUtil!!.extractAllClaims(token)
        assertThat(jwtUtil!!.isTokenExpired(claims)).isFalse
        assertThat(jwtUtil!!.getUserId(claims)).isEqualTo(user!!.id)
        assertThat(jwtUtil!!.getUserName(claims)).isEqualTo(user!!.username)
        assertThat(jwtUtil!!.getUserRole(claims)).isEqualTo(
            user!!.role
        )
        assertThat(jwtUtil!!.isTokenExpired(claims)).isFalse
    }
}