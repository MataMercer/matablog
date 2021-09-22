package com.matamercer.microblog.services;

import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Follow;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.FollowRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.web.api.v1.dto.requests.FollowRequestDto;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final FollowRepository followRepository;
    private final UserService userService;

    @Autowired
    public BlogService(BlogRepository blogRepository, UserRepository userRepository, FollowRepository followRepository, UserService userService) {
        this.blogRepository = blogRepository;
        this.followRepository = followRepository;
        this.userService = userService;
    }

    @Transactional
    public Blog save(Blog blog) {
        return blogRepository.save(blog);
    }

    @Transactional
    public void createDefaultBlogForUser(User user) {
        var blog = new Blog(user.getUsername(), user.getUsername(), false);
        blog = save(blog);
        user.addBlog(blog);
        user.setActiveBlog(blog);
        userService.save(user);
    }

    public Blog getBlog(Long id) {
        var blog = blogRepository.findById(id);
        if (!blog.isPresent()) {
            throw new NotFoundException("Blog with id " + id + " is not found.");
        } else {
            return blog.get();
        }
    }

    public Blog getBlog(String blogName) {
        var blog = blogRepository.findByBlogName(blogName);
        if (!blog.isPresent()) {
            throw new NotFoundException("Blog with name " + blogName + " is not found.");
        } else {
            return blog.get();
        }
    }

    @Transactional
    public void followBlog(Principal principal, long followeeId, FollowRequestDto followRequestDTO) {
        var currentUserBlog = getActiveBlog(principal);
        var followeeBlog = getBlog(followeeId);
        Follow follow = new Follow(currentUserBlog, followeeBlog, followRequestDTO.isNotificationsEnabled(), followRequestDTO.isMuted());
        followRepository.save(follow);
    }

    public void unfollowBlog(Principal principal, long followeeId){
        var currentUserBlog = getActiveBlog(principal);
        var followeeBlog = getBlog(followeeId);
        var follow = followRepository.findByFollowerAndFollowee(currentUserBlog, followeeBlog );
        followRepository.delete(follow);
    }

    public Blog getActiveBlog(Principal principal) {
        var user = userService.getUser(principal);
        return user.getActiveBlog();
    }

}
