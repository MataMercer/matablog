package com.matamercer.microblog.unit.repository

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authorization.UserRole
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
internal class UserRepositoryTest {
    @Autowired
    private val entityManager: TestEntityManager? = null

    @Autowired
    private val userRepository: UserRepository? = null
    private var user: User? = null
    @BeforeEach
    fun initData() {
        user = User(
            "username@gmail.com",
            "username",
            "password",
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        )
        user = entityManager!!.persist(user)
    }

    @AfterEach
    fun flushAfter() {
        entityManager!!.flush()
        entityManager.clear()
    }

    @Test
    fun whenFindByUsername_thenReturnUser() {
        val foundUser = userRepository!!.findByUsername(
            user!!.username
        )
        Assertions.assertThat(foundUser!!.get()).isEqualTo(user)
    }

    @Test
    fun whenFindByEmail_thenReturnUser() {
        val foundUser = userRepository!!.findByEmail(
            user!!.email
        )
        Assertions.assertThat(foundUser!!.get()).isEqualTo(user)
    }

    @Test
    fun whenFindByoAuth2IdAndAuthenticationProvider_thenReturnUser() {
        val foundOptionalUser = userRepository!!.findByoAuth2IdAndAuthenticationProvider(
            user!!.oAuth2Id,
            user!!.authenticationProvider
        )
        Assertions.assertThat(foundOptionalUser!!.get()).isEqualTo(user)
    }
}