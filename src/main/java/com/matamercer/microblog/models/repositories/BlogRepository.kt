package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Blog
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BlogRepository : JpaRepository<Blog?, Long?> {
    fun findByBlogName(blogName: String?): Optional<Blog?>?
}