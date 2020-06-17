package com.matamercer.microblog.services;


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
    public Page<Post> getAllPostsByPageByUserSortedByCreated(User user, int page, int pageSize){
        System.out.println("Getting posts by user...");
        log.debug("Getting posts with username");
        return postRepository.findByUser(user, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
    }
}
