package com.matamercer.microblog.services

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Follow
import com.matamercer.microblog.models.repositories.FollowRepository
import com.matamercer.microblog.web.api.v1.dto.requests.FollowRequestDto
import com.matamercer.microblog.web.error.exceptions.CircularException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class FollowService(private val followRepository: FollowRepository, private val blogService: BlogService) {
    fun followBlog(currentUserBlog: Blog, followeeId: Long, followRequestDTO: FollowRequestDto) {
        if(followeeId == currentUserBlog.id){
            throw CircularException()
        }
        val followeeBlog = blogService.getBlog(followeeId)
        val follow =
            Follow(currentUserBlog, followeeBlog, followRequestDTO.notificationsEnabled, followRequestDTO.muted)
        followRepository.save(follow)
    }

    fun getFollowers(pageRequest: PageRequest, currentUserBlog: Blog): Page<Follow> {
        return followRepository.findByFollowee(currentUserBlog, pageRequest)
    }

    fun getFollowing(pageRequest: PageRequest, currentUserBlog: Blog): Page<Follow> {
        return followRepository.findByFollower(currentUserBlog, pageRequest)
    }
    fun unfollowBlog(currentUserBlog: Blog, followeeId: Long) {
        val followeeBlog = blogService.getBlog(followeeId)
        val follow = followRepository.findByFollowerAndFollowee(currentUserBlog, followeeBlog)
        followRepository.delete(follow!!)
    }
}