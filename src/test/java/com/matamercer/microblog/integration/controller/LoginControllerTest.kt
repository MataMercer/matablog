package com.matamercer.microblog.integration.controller

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authentication.JwtConfig
import com.matamercer.microblog.security.authentication.JwtUtil
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.utilities.EnvironmentUtil
import com.matamercer.microblog.web.api.v1.dto.requests.LoginUserRequestDto
import org.junit.After
import org.junit.Before
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.net.URI
import java.net.UnknownHostException
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var environmentUtil: EnvironmentUtil

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var jwtConfig: JwtConfig

    private lateinit var testRestTemplate: TestRestTemplate
    private lateinit var user: User
    private lateinit var unencodedPassword: String

    @Before
    fun initData() {
        unencodedPassword = "password"
        user = userService.createUser(User(
            "username@gmail.com",
            "username",
            passwordEncoder.encode(unencodedPassword),
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        ))
        testRestTemplate = TestRestTemplate()
    }

    @After
    fun disposeData() {
        user.id?.let { userRepository.deleteById(it) }
    }

    @Throws(UnknownHostException::class)
    private fun getLoginRequestEntity(loginUserRequestDTO: LoginUserRequestDto): RequestEntity<LoginUserRequestDto> {
        return RequestEntity
            .post(URI.create(environmentUtil.serverUrl + "/api/v1/auth/login"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginUserRequestDTO)
    }

    @Throws(UnknownHostException::class)
    private fun getCurrentUserRequestEntity(accessToken: String): RequestEntity<Void> {
        val headers: org.springframework.http.HttpHeaders = org.springframework.http.HttpHeaders()
        headers.add(jwtConfig.authorizationHeader, accessToken)
        return RequestEntity.get(URI.create(environmentUtil.serverUrl + "/api/v1/user/currentuser"))
            .headers(headers)
            .build()
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenLogin_withGoodCredentials_thenReturnResponseIsOk_WithAccessAndRefreshTokens() {
        val requestEntity: RequestEntity<LoginUserRequestDto> =
            getLoginRequestEntity(LoginUserRequestDto(user.username!!, unencodedPassword))
        val responseEntity: ResponseEntity<Any> = testRestTemplate.exchange<Any>(requestEntity, Any::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val accessToken: Optional<String> =
            responseEntity.headers[jwtConfig.authorizationHeader]!!.stream().findFirst()
        assertThat(accessToken.isPresent).isTrue
        val refreshToken: Optional<String> =
            responseEntity.headers[jwtConfig.refreshTokenHeader]!!.stream().findFirst()
        assertThat(refreshToken.isPresent).isTrue
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenGetCurrentUser_withAccessToken_thenReturnCurrentUser() {
        val requestEntity: RequestEntity<LoginUserRequestDto> =
            getLoginRequestEntity(LoginUserRequestDto(user.username!!, unencodedPassword))
        val responseEntity: ResponseEntity<Any> = testRestTemplate.exchange<Any>(requestEntity, Any::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val accessToken: Optional<String> =
            responseEntity.headers[jwtConfig.authorizationHeader]!!.stream().findFirst()
        assertThat(accessToken.isPresent).isTrue
        val refreshToken: Optional<String> =
            responseEntity.headers[jwtConfig.refreshTokenHeader]!!.stream().findFirst()
        assertThat(refreshToken.isPresent).isTrue
        val currentUserRequestEntity: RequestEntity<Void> = getCurrentUserRequestEntity(accessToken.get())
        val currentUserResponseEntity: ResponseEntity<String> = testRestTemplate.exchange(
            currentUserRequestEntity,
            String::class.java
        )
        assertThat(currentUserResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenGetCurrentUser_withoutAccessToken_thenReturnCurrentUser() {
        val currentUserRequestEntity: RequestEntity<Void> = getCurrentUserRequestEntity("")
        val currentUserResponseEntity: ResponseEntity<String> = testRestTemplate.exchange(
            currentUserRequestEntity,
            String::class.java
        )
        assertThat(currentUserResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }
}