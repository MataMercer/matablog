package com.matamercer.microblog.services;


import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import com.matamercer.microblog.models.repositories.specifications.PostSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PostService {


    public static final String CACHE_NAME = "cache.posts";
    public static final String CACHE_NAME_PAGE = CACHE_NAME + ".page";

    private final PostRepository postRepository;
    private final PostTagService postTagService;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostTagService postTagService,
                       UserRepository userRepository,
                       FileService fileService){
        this.postRepository = postRepository;
        this.postTagService = postTagService;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    public Post createPost(CreatePostForm createPostForm, MultipartFile[] files, Principal principal ) {

        Set<PostTag> postTags = createPostForm.getPostTags().stream()
                .map(postTagService::findOrCreateByName).collect(Collectors.toSet());

        Blog blog = userRepository.findByUsername(principal.getName()).getActiveBlog();
        Post post = new Post(blog, createPostForm.getTitle(), createPostForm.getContent(),
                createPostForm.isCommunityTaggingEnabled(), createPostForm.isSensitive());

        attachFilesToPost(files, post);

        postTags.forEach(post::addPostTag);

        post =  postRepository.save(post);

        if(createPostForm.getParentPostId() != null) {
            Optional<Post> parentPost = Optional.ofNullable(getPost(Long.parseLong(createPostForm.getParentPostId())));
            if (parentPost.isPresent()) {
                post.setParentPost(parentPost.get());
                parentPost.get().addReply(post);
            }
        }

        return post;
    }

    private void attachFilesToPost(MultipartFile[] files, Post post) {
        for (MultipartFile file : files) {
            post.getAttachments().add(fileService.createFile(file, post.getBlog()));
        }
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
    public Page<Post> searchPosts(PostSearch postSearch, int page, int pageSize){
        log.debug("Getting posts with username");
        PostSpecification postSpecification = new PostSpecification(postSearch);
        return postRepository.findAll(postSpecification, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
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
