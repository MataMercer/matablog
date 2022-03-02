package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.PostTag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostTagRepository : JpaRepository<PostTag, Long> {
    fun findByName(name: String): PostTag?

    @Query("SELECT pt as postTag, COUNT(pt.id) as postTagCount FROM PostTag pt INNER JOIN pt.posts p WHERE p.blog = :blog GROUP BY pt.id")
    fun findByBlogSortedByMostUsed(blog: Blog, pageRequest: Pageable?): Page<IPostTagCount>



    interface IPostTagCount {
        val postTag: PostTag
        val postTagCount: Int
    }
}