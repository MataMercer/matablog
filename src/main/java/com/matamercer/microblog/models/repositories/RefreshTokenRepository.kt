package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface RefreshTokenRepository : JpaRepository<RefreshToken?, Long?> {
    @Query("SELECT l FROM RefreshToken l INNER JOIN l.user u WHERE u.username = :username")
    fun findByUsername(@Param("username") username: String?): RefreshToken?
}