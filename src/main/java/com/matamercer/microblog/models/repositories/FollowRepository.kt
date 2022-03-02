package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.enums.PostCategory
import com.matamercer.microblog.models.entities.PostTag
import org.springframework.data.jpa.repository.JpaRepository
import com.matamercer.microblog.models.entities.Follow

interface FollowRepository : JpaRepository<Follow?, Long?> {
    fun findByFollowerAndFollowee(follower: Blog?, followee: Blog?): Follow?
}