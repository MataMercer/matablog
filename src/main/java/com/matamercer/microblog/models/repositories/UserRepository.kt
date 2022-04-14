package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(username: String?): User?
    fun findByEmail(email: String?): User?
    fun findByoAuth2IdAndAuthenticationProvider(
        oAuth2Id: String?,
        authenticationProvider: AuthenticationProvider?
    ): User?
}