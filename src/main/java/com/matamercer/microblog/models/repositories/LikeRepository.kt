package com.matamercer.microblog.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import com.matamercer.microblog.models.entities.Like
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Post

interface LikeRepository : JpaRepository<Like, Long> {
    fun findByPostAndLiker(post: Post, liker: Blog): Like?
    fun countLikesByPost(post: Post): Long?
}