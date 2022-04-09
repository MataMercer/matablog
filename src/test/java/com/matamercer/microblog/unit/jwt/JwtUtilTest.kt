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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.util.*

@TestPropertySource("/application.properties")
@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class JwtUtilTest {

    private val jwtConfigSecretKey: String = "securesecuresecuresecuresecuresecuresecuresecuresecuresecuresecure"

    private val jwtConfigTokenPrefix: String = "Bearer"

    private val jwtConfigRefreshTokenExpirationInDays: Int = 10

    private val getJwtConfigAccessTokenExpirationInHours: Int = 12

    private lateinit var user: User

    @MockK
    private lateinit var jwtUtil: JwtUtil

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @BeforeEach
    fun setup() {
        clearAllMocks()
        user = User(
            "username@gmail.com",
            "username",
            "password",
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        )
        user.id = 0L
        val jwtConfig = JwtConfig()
        jwtConfig.secretKey = jwtConfigSecretKey
        jwtConfig.tokenPrefix = jwtConfigTokenPrefix
        jwtConfig.refreshTokenExpirationInDays = jwtConfigRefreshTokenExpirationInDays
        jwtConfig.accessTokenExpirationInHours = getJwtConfigAccessTokenExpirationInHours
        val jwtSecretKey = JwtSecretKey(jwtConfig)
        val secretKey = jwtSecretKey.secretKey
        every { userRepository.findById(0L) } returns Optional.ofNullable(user)
        every {
            userRepository.findByUsername(
                user.username
            )
        } returns Optional.ofNullable(user)
        every { userRepository.findById(user.id!!) } returns Optional.ofNullable(user)
        val refreshToken = RefreshToken(user)
        refreshToken.id = 0L
        every { refreshTokenRepository.save(any()) } returns refreshToken

        jwtUtil = JwtUtil(secretKey, jwtConfig, userRepository, refreshTokenRepository)
    }

    @Test
    fun whenCreateAccessToken_returnValidAccessToken() {
        val token = jwtUtil.createAccessToken(user.id!!)
        val claims = jwtUtil.extractAllClaims(token)
        assertThat(jwtUtil.getUserId(claims)).isEqualTo(user.id)
        assertThat(jwtUtil.getUserName(claims)).isEqualTo(user.username)
        assertThat(jwtUtil.isTokenExpired(claims)).isFalse
        assertThat(jwtUtil.getUserRole(claims)).isEqualTo(
            user.role
        )
    }

    @Test
    fun whenCreateRefreshToken_returnValidRefreshToken() {
        val token = jwtUtil.createRefreshToken(user.id!!)
        val claims = jwtUtil.extractAllClaims(token)
        assertThat(jwtUtil.isTokenExpired(claims)).isFalse
        assertThat(jwtUtil.getUserId(claims)).isEqualTo(user.id)
        assertThat(jwtUtil.getUserName(claims)).isEqualTo(user.username)
        assertThat(jwtUtil.getUserRole(claims)).isEqualTo(
            user.role
        )
        assertThat(jwtUtil.isTokenExpired(claims)).isFalse
    }
}