package com.matamercer.microblog.services;


import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.enums.PostCategory;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import com.matamercer.microblog.models.repositories.specifications.PostSpecification;
import com.matamercer.microblog.web.api.v1.forms.CreatePostForm;
import com.matamercer.microblog.web.api.v1.forms.UpdatePostForm;
import com.matamercer.microblog.web.error.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
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
    private final BlogRepository blogRepository;
    private final FileService fileService;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostTagService postTagService,
                       UserRepository userRepository,
                       BlogRepository blogRepository, FileService fileService){
        this.postRepository = postRepository;
        this.postTagService = postTagService;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.fileService = fileService;
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    public Post createPost(CreatePostForm createPostForm, MultipartFile[] files, Principal principal ) {

        Set<PostTag> postTags = createPostForm.getPostTags().stream()
                .map(postTagService::findOrCreateByName).collect(Collectors.toSet());

        var optionalUser = userRepository.findByUsername(principal.getName());
        if(!optionalUser.isPresent()){
            throw new UserNotFoundException("Unable to create post because unable to find logged in user.");
        }
        User user = optionalUser.get();
        Blog blog = user.getActiveBlog();
        Post post = new Post(blog, createPostForm.getTitle(), createPostForm.getContent(),
                createPostForm.isCommunityTaggingEnabled(), createPostForm.isSensitive(), createPostForm.isPublished());
        if(files != null) {
            attachFilesToPost(files, post);
        }
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
    public Post updatePost(UpdatePostForm updatePostForm, MultipartFile[] files, Principal principal){
        return null;
    }


    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#post.id"),
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    public void deletePost(Post post){
        postRepository.delete(post);
    }

//    @Cacheable(value = CACHE_NAME_PAGE, key = "T(java.lang.String).valueOf(#page).concat('-').concat(#pageSize)")
    public Page<Post> searchPosts(Optional<String> optionalBlogName, Optional<String> optionalCategory, Optional<List<String>> optionalTagNames, PageRequest pageRequest){
        PostSearch postSearch = new PostSearch();
        if(optionalBlogName.isPresent()){
            val blogName = optionalBlogName.get();
            Blog blog = blogRepository.findByBlogName(blogName);
            if(blog == null){
                throw new NotFoundException("Blog not found");
            }
            postSearch.setBlog(blog);
        }

        if(optionalTagNames.isPresent()) {
            val tagNames = optionalTagNames.get();
            Set<PostTag> postTags = postTagService.getTags(tagNames);
            postSearch.setPostTags(postTags);
        }
        if(optionalCategory.isPresent()) {
            val category = optionalCategory.get();
            postSearch.setPostCategory(PostCategory.valueOf(category));
        }else{
            postSearch.setPostCategory(PostCategory.ROOT);
        }

        PostSpecification postSpecification = new PostSpecification(postSearch);
        return postRepository.findAll(postSpecification, pageRequest);
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
