package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Follow;
import com.matamercer.microblog.models.repositories.FollowRepository;
import com.matamercer.microblog.web.api.v1.dto.requests.FollowRequestDto;
import lombok.var;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class FollowService {
    private final FollowRepository followRepository;
    private final BlogService blogService;

    public FollowService(FollowRepository followRepository, BlogService blogService) {
        this.followRepository = followRepository;
        this.blogService = blogService;
    }

    public void followBlog(Blog currentUserBlog, long followeeId, FollowRequestDto followRequestDTO) {
        var followeeBlog = blogService.getBlog(followeeId);
        var follow = new Follow(currentUserBlog, followeeBlog, followRequestDTO.isNotificationsEnabled(), followRequestDTO.isMuted());
        followRepository.save(follow);
    }

    public void unfollowBlog(Blog currentUserBlog, long followeeId){
        var followeeBlog = blogService.getBlog(followeeId);
        var follow = followRepository.findByFollowerAndFollowee(currentUserBlog, followeeBlog );
        followRepository.delete(follow);
    }
}
