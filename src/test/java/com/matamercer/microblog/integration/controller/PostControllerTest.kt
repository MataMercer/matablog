package com.matamercer.microblog.integration.controller

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.UserService
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockKExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostControllerTest(
    private val userService: UserService?
) {
    private var user: User? = null
    private var testRestTemplate: TestRestTemplate? = null

    @BeforeEach
    fun initData() {
        user = User(
            "username@gmail.com",
            "username",
            "",
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        )
        user = userService!!.createUser(user!!)
        testRestTemplate = TestRestTemplate()
    }

    @AfterEach
    fun disposeData() {
        userService!!.delete(user!!)
    }

//    @Test
//    fun createPost_thenReturnOk() {
//    }
}