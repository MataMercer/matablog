package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BlogService {
    private final BlogRepository blogRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
    }

    @Transactional
    public Blog createBlog(Blog blog){
        return blogRepository.save(blog);
    }

    @Transactional
    public Blog createDefaultBlogForUser(User user){
        Blog blog = new Blog(user.getUsername(), user.getUsername(), false);
        return createBlog(blog);
    }

}
