package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Follow
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository : JpaRepository<Follow?, Long?> {
    fun findByFollowerAndFollowee(follower: Blog?, followee: Blog?): Follow?
}