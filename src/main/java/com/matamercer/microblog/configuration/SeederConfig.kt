package com.matamercer.microblog.configuration

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.BlogService
import com.matamercer.microblog.services.PostService
import com.matamercer.microblog.services.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SeederConfig(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    private val postService: PostService,
    private val blogService: BlogService
) {
    @Bean
    fun seedData(): CommandLineRunner {
        return CommandLineRunner {
            val adminUser = User(
                email = "developer.mercer@googlemail.com",
                username = "a",
                password = passwordEncoder.encode("1"),
                role = UserRole.ADMIN,
                authenticationProvider = AuthenticationProvider.LOCAL
            )
            addUserToApp(adminUser)
            val bloggerUser = User(
                email = "mercer233@gmail.com",
                username = "b",
                password = passwordEncoder.encode("1"),
                role = UserRole.BLOGGER,
                authenticationProvider = AuthenticationProvider.LOCAL
            )
            addUserToApp(bloggerUser)
            val readerUser = User(
                email = "mercer233@googlemail.com",
                username = "c",
                password = passwordEncoder.encode("1"),
                role = UserRole.READER,
                authenticationProvider = AuthenticationProvider.LOCAL
            )
            addUserToApp(readerUser)
        }
    }

    private fun addUserToApp(user: User) {
        val foundUser = userRepository.findByEmail(user.email)
        val userEntity = userService.createUser(user)
        blogService.createDefaultBlogForUser(userEntity)

    }
}