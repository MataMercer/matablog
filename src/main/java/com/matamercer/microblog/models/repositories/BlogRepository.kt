package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Blog
import org.springframework.data.jpa.repository.JpaRepository

interface BlogRepository : JpaRepository<Blog?, Long?> {
    fun findByBlogName(blogName: String?): Blog?
}