package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Follow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<Follow?, Long?> {
    fun findByFollowerAndFollowee(follower: Blog?, followee: Blog?): Follow?
    fun findByFollowee(followee: Blog?, pageable: Pageable): Page<Follow>
    fun findByFollower(follower: Blog?, pageable: Pageable): Page<Follow>
}