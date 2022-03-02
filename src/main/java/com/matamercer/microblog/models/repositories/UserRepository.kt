package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(username: String?): Optional<User>
    fun findByEmail(email: String?): Optional<User>
    fun findByoAuth2IdAndAuthenticationProvider(oAuth2Id: String?, authenticationProvider: AuthenticationProvider?): Optional<User>
}