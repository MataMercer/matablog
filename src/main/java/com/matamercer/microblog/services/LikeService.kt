package com.matamercer.microblog.services

import com.matamercer.microblog.events.onNotificationEvent
import com.matamercer.microblog.exceptions.NotFoundException
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Like
import com.matamercer.microblog.models.entities.Notification
import com.matamercer.microblog.models.repositories.LikeRepository
import com.matamercer.microblog.web.error.exceptions.AlreadyExistsException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class LikeService(private val likeRepository: LikeRepository,
                  private val postService: PostService,
                  private val applicationEventPublisher: ApplicationEventPublisher) {
    fun likePost(blog: Blog, postId: Long) {
        val existingLike = likeRepository.findByPostAndLiker(postService.getPost(postId), blog)
        if (existingLike != null) {
            throw AlreadyExistsException()
        }
        val post = postService.getPost(postId)
        val like = Like(blog, post )
        val liked = likeRepository.save(like)
        // Hard nuisance version
        val user = post.blog?.users?.first { it.activeBlog?.blogName == post.blog!!.blogName }
        applicationEventPublisher
            .publishEvent(onNotificationEvent(Notification(user?.id!!,blog.blogName+" liked your post "+post.title)))
    }

    fun unlikePost(blog: Blog, postId: Long) {
        likeRepository.delete(findByPostIdAndLiker(postId, blog)!!)
    }

    fun findByPostIdAndLiker(postId: Long, blog: Blog): Like? {
        return likeRepository.findByPostAndLiker(postService.getPost(postId), blog) ?: throw NotFoundException("Like not found bro")
    }
}