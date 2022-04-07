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
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.net.URI
import java.net.UnknownHostException

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest(
    private val userRepository: UserRepository?,
    private val passwordEncoder: PasswordEncoder?,
    private val userService: UserService?,
    private val environmentUtil: EnvironmentUtil?,
    private val jwtUtil: JwtUtil,
    private val jwtConfig: JwtConfig?,
) {

    private var testRestTemplate: TestRestTemplate? = null
    private var user: User? = null
    private var unencodedPassword: String? = null

    @Before
    fun initData() {
        unencodedPassword = "password"
        user = User(
            "username@gmail.com",
            "username",
            passwordEncoder!!.encode(unencodedPassword),
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        )
        user = userService!!.createUser(user!!)
        testRestTemplate = TestRestTemplate()
    }

    @After
    fun disposeData() {
        userRepository!!.deleteById(user!!.id)
    }

    @Throws(UnknownHostException::class)
    private fun getLoginRequestEntity(loginUserRequestDTO: LoginUserRequestDto): RequestEntity<LoginUserRequestDto> {
        return RequestEntity
            .post(URI.create(environmentUtil!!.serverUrl + "/api/v1/auth/login"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginUserRequestDTO)
    }

    @Throws(UnknownHostException::class)
    private fun getCurrentUserRequestEntity(accessToken: String): RequestEntity<Void> {
        val headers = HttpHeaders()
        headers.add(jwtConfig!!.authorizationHeader, accessToken)
        return RequestEntity.get(URI.create(environmentUtil!!.serverUrl + "/api/v1/user/currentuser"))
            .headers(headers)
            .build()
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenLogin_withGoodCredentials_thenReturnResponseIsOk_WithAccessAndRefreshTokens() {
        val requestEntity = getLoginRequestEntity(
            LoginUserRequestDto(
                user!!.username!!,
                unencodedPassword!!
            )
        )
        val responseEntity = testRestTemplate!!.exchange(requestEntity, Any::class.java)
        Assertions.assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val accessToken = responseEntity.headers[jwtConfig!!.authorizationHeader]!!
            .stream().findFirst()
        Assertions.assertThat(accessToken.isPresent).isTrue
        val refreshToken = responseEntity.headers[jwtConfig.refreshTokenHeader]!!
            .stream().findFirst()
        Assertions.assertThat(refreshToken.isPresent).isTrue
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenGetCurrentUser_withAccessToken_thenReturnCurrentUser() {
        val requestEntity = getLoginRequestEntity(
            LoginUserRequestDto(
                user!!.username!!,
                unencodedPassword!!
            )
        )
        val responseEntity = testRestTemplate!!.exchange(requestEntity, Any::class.java)
        Assertions.assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val accessToken = responseEntity.headers[jwtConfig!!.authorizationHeader]!!
            .stream().findFirst()
        Assertions.assertThat(accessToken.isPresent).isTrue
        val refreshToken = responseEntity.headers[jwtConfig.refreshTokenHeader]!!
            .stream().findFirst()
        Assertions.assertThat(refreshToken.isPresent).isTrue
        val currentUserRequestEntity = getCurrentUserRequestEntity(accessToken.get())
        val currentUserResponseEntity = testRestTemplate!!.exchange(
            currentUserRequestEntity,
            String::class.java
        )
        Assertions.assertThat(currentUserResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenGetCurrentUser_withoutAccessToken_thenReturnCurrentUser() {
        val currentUserRequestEntity = getCurrentUserRequestEntity("")
        val currentUserResponseEntity = testRestTemplate!!.exchange(
            currentUserRequestEntity,
            String::class.java
        )
        Assertions.assertThat(currentUserResponseEntity.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }
}