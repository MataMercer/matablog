package com.matamercer.microblog.integration.controller

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.BlogRepository
import com.matamercer.microblog.models.repositories.RefreshTokenRepository
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authentication.JwtConfig
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.utilities.EnvironmentUtil
import com.matamercer.microblog.web.api.v1.dto.requests.LoginUserRequestDto
import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.net.URI
import java.net.UnknownHostException

@ExtendWith(MockKExtension::class, SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest @Autowired constructor(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtConfig: JwtConfig,
    private val environmentUtil: EnvironmentUtil,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val blogRepository: BlogRepository
) {
    private var testRestTemplate: TestRestTemplate? = null

    private lateinit var user: User

    private lateinit var responseEntity: ResponseEntity<Any>

    private lateinit var requestEntity: RequestEntity<LoginUserRequestDto>

    private var unencodedPassword: String? = null

    @BeforeEach
    fun initData() {
        clearAllMocks()
        userRepository.deleteAll()
        refreshTokenRepository.deleteAll()
        blogRepository.deleteAll()
        unencodedPassword = "password"
        user = User(
            email = "username@gmail.com",
            username = "username",
            password = passwordEncoder.encode(unencodedPassword),
            role = UserRole.BLOGGER,
            authenticationProvider = AuthenticationProvider.LOCAL
        )
        val blog = Blog(isSensitive = true, preferredBlogName = "Testermourne", blogName = "Tester Agony")
        blogRepository.save(blog)
        user.activeBlog = blog
        user = userService.createUser(user)
        testRestTemplate = TestRestTemplate()
        requestEntity = getLoginRequestEntity(
            LoginUserRequestDto(
                user.username!!,
                unencodedPassword!!
            )
        )
        responseEntity = testRestTemplate!!.exchange(requestEntity, Any::class.java)
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
        val headers = HttpHeaders()
        headers.add(jwtConfig.authorizationHeader, accessToken)
        return RequestEntity.get(URI.create(environmentUtil.serverUrl + "/api/v1/user/currentuser"))
            .headers(headers)
            .build()
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenLogin_withGoodCredentials_thenReturnResponseIsOk_WithAccessAndRefreshTokens() {
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        val accessToken = responseEntity.headers[jwtConfig.authorizationHeader]!!.first()
        assertThat(accessToken != null).isTrue
        val refreshToken = responseEntity.headers[jwtConfig.refreshTokenHeader]!!.first()
        assertThat(refreshToken != null).isTrue
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenGetCurrentUser_withAccessToken_thenReturnCurrentUser() {
        val accessToken = responseEntity.headers[jwtConfig.authorizationHeader]!!.first()
        assertThat(accessToken != null).isTrue
        val refreshToken = responseEntity.headers[jwtConfig.refreshTokenHeader]!!.first()
        assertThat(refreshToken != null).isTrue
        val currentUserRequestEntity = getCurrentUserRequestEntity(accessToken)
        val currentUserWithTokenResponseEntity = testRestTemplate!!.exchange(
            currentUserRequestEntity,
            String::class.java
        )
        assertThat(currentUserWithTokenResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @Throws(UnknownHostException::class)
    fun whenGetCurrentUser_withoutAccessToken_thenReturnCurrentUser() {
        val currentUserWithOutTokenResponseEntity = testRestTemplate!!.exchange(
            getCurrentUserRequestEntity(""),
            String::class.java
        )
        assertThat(currentUserWithOutTokenResponseEntity.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }
}