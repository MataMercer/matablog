package com.matamercer.microblog.models.repositories

import org.springframework.data.jpa.repository.JpaRepository
import com.matamercer.microblog.models.entities.Post
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long>, JpaSpecificationExecutor<Post>