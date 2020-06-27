package com.matamercer.microblog.services;


import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class PostService {


    public static final String CACHE_NAME = "cache.posts";
    public static final String CACHE_NAME_PAGE = CACHE_NAME + ".page";

    @Autowired
    private PostRepository postRepository;

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#post.id"),
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true)
    })
    public Post updatePost(Post post){
        return postRepository.save(post);
    }


    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#post.id"),
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    public void deletePost(Post post){
        postRepository.delete(post);
    }

//    public Page<Post> getAllPostsByPageSortedByCreatedAt(int page, int pageSize){
//        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
//    }

    @Cacheable(value = CACHE_NAME_PAGE, key = "T(java.lang.String).valueOf(#page).concat('-').concat(#pageSize)")
    public Page<Post> getAllPostsByPageByBlogSortedByCreated(Blog blog, int page, int pageSize){
        System.out.println("Getting posts by user...");
        log.debug("Getting posts with username");
        return postRepository.findByBlog(blog, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
    }

    @Cacheable(CACHE_NAME)
    public Post getPost(Long postId) {
        log.debug("Get post " + postId);

        Optional<Post> post = postRepository.findById(postId);

        if (!post.isPresent()) {
            throw new NotFoundException("Post with id " + postId + " is not found.");
        }else{
            return post.get();
        }
    }
}
