package com.matamercer.microblog.services;


import com.google.common.collect.Sets;
import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.enums.PostCategory;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import com.matamercer.microblog.models.repositories.specifications.PostSpecification;
import com.matamercer.microblog.security.authorization.UserAuthority;
import com.matamercer.microblog.web.api.v1.dto.mappers.request.PostRequestDtoMapper;
import com.matamercer.microblog.web.api.v1.dto.mappers.response.PostResponseDtoMapper;
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto;
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
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
    private final BlogService blogService;
    private final FileService fileService;
    private final PostResponseDtoMapper postResponseDtoMapper;
    private final PostRequestDtoMapper postRequestDtoMapper;


    @Autowired
    public PostService(PostRepository postRepository,
                       PostTagService postTagService,
                       BlogService blogService, FileService fileService,
                       PostResponseDtoMapper postResponseDtoMapper,
                       PostRequestDtoMapper postRequestDtoMapper){
        this.postRepository = postRepository;
        this.postTagService = postTagService;
        this.blogService = blogService;
        this.fileService = fileService;
        this.postResponseDtoMapper = postResponseDtoMapper;
        this.postRequestDtoMapper = postRequestDtoMapper;
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).POST_CREATE_NEW.toString())")
    public PostResponseDto createNewPost(PostRequestDto postRequestDTO, MultipartFile[] files, Blog blog ) {
        Post post = createPostHelper(postRequestDTO, files, blog);
        return postResponseDtoMapper.map(post);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).POST_CREATE_COMMENT.toString())")
    public PostResponseDto createReplyPost(PostRequestDto postRequestDTO, MultipartFile[] files, Blog blog ) {
        Post post = createPostHelper(postRequestDTO, files, blog);
        addReplyToParentPost(Long.parseLong(postRequestDTO.getParentPostId()), post);
        return postResponseDtoMapper.map(post);
    }

    private Post createPostHelper(PostRequestDto postRequestDTO, MultipartFile[] files, Blog blog){
        Post post = postRequestDtoMapper.map(postRequestDTO);
        post.setBlog(blog);

        attachFilesToPost(files, post);

        if(postRequestDTO.getPostTags() != null) {
            Set<PostTag> postTags = postRequestDTO.getPostTags().stream()
                    .map(postTagService::findOrCreateByName).collect(Collectors.toSet());
            postTags.forEach(post::addPostTag);
        }

        return postRepository.save(post);

    }


    private void addReplyToParentPost(long parentPostId, Post replyPost){
        Post parentPost = getPost(parentPostId);
            replyPost.setParentPost(parentPost);
            parentPost.addReply(replyPost);
            postRepository.save(replyPost);
            postRepository.save(parentPost);
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#post.id"),
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true)
    })
    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).POST_UPDATE.toString())")
    public PostResponseDto updatePost(PostRequestDto updatePostRequest, MultipartFile[] files, Blog blog){
        Post updatedPost = postRequestDtoMapper.map(updatePostRequest);
        Post post = getPost(updatedPost.getId());
        checkOwnership(post);

        post.setAttachments(updatePostRequest.getAttachments().stream().map(attachmentId -> {
            File file = new File();
            file.setId(attachmentId);
            return file;
        }).collect(Collectors.toList()));
        Set<File> filesToDelete = Sets.difference(new HashSet<>(post.getAttachments()), new HashSet<Long>(updatePostRequest.getAttachments()));
        filesToDelete.forEach(fileService::deleteFile);

        attachFilesToPost(files, post);

        if(updatePostRequest.getPostTags() != null) {
            Set<PostTag> postTags = updatePostRequest.getPostTags().stream()
                    .map(postTagService::findOrCreateByName).collect(Collectors.toSet());
            postTags.forEach(post::addPostTag);
        }

        post = postRepository.save(post);

        return postResponseDtoMapper.map(post);
    }

    private void attachFilesToPost(MultipartFile[] files, Post post) {
        if(files != null) {
            for (MultipartFile file : files) {
                post.getAttachments().add(fileService.createFile(file, post.getBlog()));
            }
        }
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#post.id"),
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).POST_UPDATE.toString())")
    public void deletePost(Post post){
        checkOwnership(post);
        postRepository.delete(post);
    }


    @PreAuthorize("hasAuthority(T(com.matamercer.microblog.security.authorization.UserAuthority).POST_UPDATE.toString())")
    public void deletePost(long id){
        checkOwnership(getPost(id));
        postRepository.deleteById(id);
    }

//    @Cacheable(value = CACHE_NAME_PAGE, key = "T(java.lang.String).valueOf(#page).concat('-').concat(#pageSize)")
    public Page<PostResponseDto> searchPosts(Optional<String> optionalBlogName, Optional<String> optionalCategory, Optional<List<String>> optionalTagNames, PageRequest pageRequest){
        PostSearch postSearch = new PostSearch();
        if(optionalBlogName.isPresent()){
            val blogName = optionalBlogName.get();
            Blog blog = blogService.getBlog(blogName);
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
        val posts =  postRepository.findAll(postSpecification, pageRequest);
        return posts.map(postResponseDtoMapper::map);
    }

    @Cacheable(CACHE_NAME)
    public Post getPost(Long postId) {
        log.debug("Get post " + postId);
        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            throw new NotFoundException("Post with id " + postId + " is not found.");
        }else{
            if(!post.get().isPublished()){
                checkOwnership(post.get());
            }
            return post.get();
        }
    }

    public PostResponseDto getPostResponseDto(Long postId){
        Post post = getPost(postId);
        return postResponseDtoMapper.map(post);
    }



    private void checkOwnership(Post post){
        boolean isAnon = SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        if(isAnon){
            throw new AccessDeniedException("User is anonymous.");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!post.getBlog().equals(user.getActiveBlog()) && !user.hasAuthority(UserAuthority.POST_MANAGE )){
            throw new AccessDeniedException("User does not own post");
        }
    }

}
