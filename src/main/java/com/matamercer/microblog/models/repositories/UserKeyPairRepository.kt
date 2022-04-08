package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.entities.UserKeyPair
import org.springframework.data.jpa.repository.JpaRepository


interface UserKeyPairRepository : JpaRepository<UserKeyPair?, Long?> {
    fun findByUser(user: User?): UserKeyPair?
}