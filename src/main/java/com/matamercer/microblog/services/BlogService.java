package com.matamercer.microblog.services;

import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.web.api.v1.dto.mappers.response.BlogResponseDtoMapper;
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserService userService;
    private final BlogResponseDtoMapper blogResponseDtoMapper;

    @Autowired
    public BlogService(BlogRepository blogRepository, UserService userService, BlogResponseDtoMapper blogResponseDtoMapper) {
        this.blogRepository = blogRepository;
        this.userService = userService;
        this.blogResponseDtoMapper = blogResponseDtoMapper;
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
        val blog = blogRepository.findById(id);
        if (!blog.isPresent()) {
            throw new NotFoundException("Blog with id " + id + " is not found.");
        } else {
            return blog.get();
        }
    }

    public Blog getBlog(String blogName) {
        val blog = blogRepository.findByBlogName(blogName);
        if (!blog.isPresent()) {
            throw new NotFoundException("Blog with name " + blogName + " is not found.");
        } else {
            return blog.get();
        }
    }

    public BlogResponseDto getBlogResponseDto(long id){
        Blog blog = getBlog(id);
        return blogResponseDtoMapper.map(blog);
    }
}
