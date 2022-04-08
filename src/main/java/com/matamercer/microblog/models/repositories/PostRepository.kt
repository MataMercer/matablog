package com.matamercer.microblog.models.repositories

import com.matamercer.microblog.models.entities.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor


interface PostRepository : JpaRepository<Post, Long>, JpaSpecificationExecutor<Post>