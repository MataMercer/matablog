package com.matamercer.microblog.integration.controller

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.UserService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostControllerTest(
    private val userService: UserService?
) {
    private var user: User? = null
    private var testRestTemplate: TestRestTemplate? = null
    @Before
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

    @After
    fun disposeData() {
        userService!!.delete(user!!)
    }

    @Test
    fun createPost_thenReturnOk() {
    }
}