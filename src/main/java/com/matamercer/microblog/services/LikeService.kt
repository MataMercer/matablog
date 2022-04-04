package com.matamercer.microblog.services

import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Like
import com.matamercer.microblog.models.repositories.LikeRepository
import com.matamercer.microblog.web.error.exceptions.AlreadyExistsException
import org.springframework.stereotype.Service

@Service
class LikeService(private val likeRepository: LikeRepository, private val postService: PostService) {
    fun likePost(blog: Blog, postId: Long) {
        val existingLike = likeRepository.findByPostAndLiker(postService.getPost(postId), blog)
        if (existingLike != null) {
            throw AlreadyExistsException()
        }
        val like = Like(blog, postService.getPost(postId))
        val liked = likeRepository.save(like)
    }

    fun unlikePost(blog: Blog, postId: Long) {
        likeRepository.delete(findByPostIdAndLiker(postId, blog))
    }

    fun findByPostIdAndLiker(postId: Long, blog: Blog): Like? {
        return likeRepository.findByPostAndLiker(postService.getPost(postId), blog) ?: throw NotFoundException("Like not found bro")
    }
}