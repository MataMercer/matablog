package com.matamercer.microblog.configuration

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.services.PostService
import com.matamercer.microblog.services.BlogService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SeederConfig(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder, private val userService: UserService, private val postService: PostService, private val blogService: BlogService) {
    @Bean
    fun seedData(): CommandLineRunner {
        return CommandLineRunner {
            val adminUser = User(
                    "developer.mercer@googlemail.com",
                    "a",
                    passwordEncoder.encode("1"),
                    UserRole.ADMIN,
                    AuthenticationProvider.LOCAL
            )
            addUserToApp(adminUser)
            val bloggerUser = User(
                    "mercer233@gmail.com",
                    "b",
                    passwordEncoder.encode("1"),
                    UserRole.BLOGGER,
                    AuthenticationProvider.LOCAL
            )
            addUserToApp(bloggerUser)
            val readerUser = User(
                    "mercer233@googlemail.com",
                    "c",
                    passwordEncoder.encode("1"),
                    UserRole.READER,
                    AuthenticationProvider.LOCAL
            )
            addUserToApp(readerUser)
        }
    }

    private fun addUserToApp(user: User) {
        val foundUser = userRepository.findByEmail(user.email)
        if (!foundUser.isPresent) {
            val userEntity = userService.createUser(user)
            blogService.createDefaultBlogForUser(userEntity)
        }
    }
}