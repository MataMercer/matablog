package com.matamercer.microblog.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import com.matamercer.microblog.models.entities.RefreshToken
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken?, Long?> {
    @Query("SELECT l FROM RefreshToken l INNER JOIN l.user u WHERE u.username = :username")
    fun findByUsername(@Param("username") username: String?): RefreshToken?
}