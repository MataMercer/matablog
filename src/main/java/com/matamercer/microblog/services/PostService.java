package com.matamercer.microblog.services;


import com.matamercer.microblog.Exceptions.NotFoundException;
import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.enums.PostCategory;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import com.matamercer.microblog.models.repositories.specifications.PostSpecification;
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto;
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final BlogRepository blogRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostTagService postTagService,
                       BlogRepository blogRepository,
                       FileService fileService,
                       ModelMapper modelMapper){
        this.postRepository = postRepository;
        this.postTagService = postTagService;
        this.blogRepository = blogRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
    }

    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true),
    })
    public Post createPost(PostRequestDto postRequestDTO, MultipartFile[] files, User user ) {

        Blog blog = user.getActiveBlog();
        Post post = convertDtoRequestToEntity(postRequestDTO);
        post.setBlog(blog);

        if(files != null) {
            attachFilesToPost(files, post);
        }

        if(postRequestDTO.getPostTags() != null) {
            Set<PostTag> postTags = postRequestDTO.getPostTags().stream()
                    .map(postTagService::findOrCreateByName).collect(Collectors.toSet());
            postTags.forEach(post::addPostTag);
        }

        post =  postRepository.save(post);

        if(postRequestDTO.getParentPostId() != null) {
            Optional<Post> parentPost = Optional.ofNullable(getPost(Long.parseLong(postRequestDTO.getParentPostId())));
            if (parentPost.isPresent()) {
                post.setParentPost(parentPost.get());
                parentPost.get().addReply(post);
                postRepository.save(post);
                postRepository.save(parentPost.get());
            }
        }
        return post;
    }

    private void attachFilesToPost(MultipartFile[] files, Post post) {
        for (MultipartFile file : files) {
            post.getAttachments().add(fileService.createFile(file, post.getBlog()));
        }
    }

//    @Caching(evict = {
//            @CacheEvict(value = CACHE_NAME, key = "#post.id"),
//            @CacheEvict(value = CACHE_NAME_PAGE, allEntries = true)
//    })
//    public Post updatePost(UpdatePostRequestDto updatePostRequest, MultipartFile[] files, Principal principal){
//        return null;
//    }


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
            Optional<Blog> optionalBlog = blogRepository.findByBlogName(blogName);
            if(!optionalBlog.isPresent()){
                throw new NotFoundException("Blog not found");
            }
            postSearch.setBlog(optionalBlog.get());
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

    private long parseId(String id){
        return Long.parseLong(id);
    }

    private Post convertDtoRequestToEntity(PostRequestDto postDto) {
        TypeMap<PostRequestDto, Post> postRequestDtoToPostTypeMap = modelMapper.createTypeMap(PostRequestDto.class, Post.class);
        postRequestDtoToPostTypeMap.addMappings(mapper -> mapper.skip(Post::setPostTags));
        postRequestDtoToPostTypeMap.addMappings(mapper -> mapper.skip(Post::setId));
        Post post =  postRequestDtoToPostTypeMap.map(postDto);
        if(postDto.getId() != null) {
            post.setId(Long.parseLong(postDto.getId()));
        }
        return post;
    }

    public PostResponseDto convertEntityToDtoResponse(Post post){
        TypeMap<Post, PostResponseDto> postToPostResponseDtoTypeMap = modelMapper.createTypeMap(Post.class, PostResponseDto.class);
        PostResponseDto postResponseDto = postToPostResponseDtoTypeMap.map(post);
        return postResponseDto;
    }
}
