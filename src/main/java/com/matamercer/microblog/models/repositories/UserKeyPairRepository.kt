package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import com.matamercer.microblog.models.entities.UserKeyPair
import org.springframework.stereotype.Repository

@Repository
interface UserKeyPairRepository : JpaRepository<UserKeyPair?, Long?> {
    fun findByUser(user: User?): UserKeyPair?
}