package com.matamercer.microblog.services

import com.google.common.collect.Sets
import com.matamercer.microblog.Exceptions.NotFoundException
import com.matamercer.microblog.models.entities.*
import com.matamercer.microblog.models.enums.PostCategory
import com.matamercer.microblog.models.repositories.PostRepository
import com.matamercer.microblog.models.repositories.searches.PostSearch
import com.matamercer.microblog.models.repositories.specifications.PostSpecification
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.security.authorization.UserAuthority
import com.matamercer.microblog.web.api.v1.dto.mappers.toPost
import com.matamercer.microblog.web.api.v1.dto.mappers.toPostResponseDto
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class PostService @Autowired constructor(
    private val postRepository: PostRepository,
    private val postTagService: PostTagService,
    private val blogService: BlogService, private val fileService: FileService,
) {
    @Caching(evict = [CacheEvict(value = arrayOf(CACHE_NAME_PAGE), allEntries = true)])
    fun createNewPost(postRequestDTO: PostRequestDto, files: Array<MultipartFile>, blog: Blog): PostResponseDto {
        val post = createPostHelper(postRequestDTO, files, blog)
        return post.toPostResponseDto()
    }

    @Caching(evict = [CacheEvict(value = arrayOf(CACHE_NAME_PAGE), allEntries = true)])
    fun createReplyPost(postRequestDTO: PostRequestDto, files: Array<MultipartFile>, blog: Blog): PostResponseDto {
        val post = createPostHelper(postRequestDTO, files, blog)
        postRequestDTO.parentPostId?.let { addReplyToParentPost(it.toLong(), post) }
        return post.toPostResponseDto()
    }

    private fun createPostHelper(postRequestDTO: PostRequestDto, files: Array<MultipartFile>, blog: Blog): Post {
        val post = postRequestDTO.toPost()
        post.blog = blog
        attachFilesToPost(files, post)
        val postTags = postRequestDTO.postTags
            ?.map { postTagService.findOrCreateByName(it) }?.toSet()
            ?.forEach { post.addPostTag(it) }
        return postRepository.save(post)
    }

    private fun addReplyToParentPost(parentPostId: Long, replyPost: Post) {
        val parentPost = getPost(parentPostId)
        replyPost.parentPost = parentPost
        parentPost.addReply(replyPost)
        postRepository.save(replyPost)
        postRepository.save(parentPost)
    }

    @Caching(
        evict = [CacheEvict(value = arrayOf(CACHE_NAME), key = "#post.id"), CacheEvict(
            value = arrayOf(CACHE_NAME_PAGE),
            allEntries = true
        )]
    )
    fun updatePost(updatePostRequest: PostRequestDto, files: Array<MultipartFile>, blog: Blog?): PostResponseDto {
        var post = getPost(updatePostRequest.id?.toLong())
        checkOwnership(post)

        post.title = updatePostRequest.title
        post.content = updatePostRequest.content
        post.isSensitive = updatePostRequest.sensitive ?: false
        post.published = updatePostRequest.published ?: false

        val fileIdsToDelete: Set<Long> =
            Sets.difference(HashSet(post.attachments.map { it.id }), HashSet(updatePostRequest.attachments ?: emptyList()))
        fileIdsToDelete.forEach {
            post.attachments.remove(fileService.getFile(it))
            fileService.deleteFile(it)
        }

        val validReordering = updatePostRequest.attachments?.all { it -> post.attachments.contains(fileService.getFile(it)) }
        if (validReordering == true){
            post.attachments = updatePostRequest.attachments.map { fileService.getFile(it) }.toMutableList()
            updatePostRequest.attachmentInsertions?.let { attachFilesToPost(files, post, it.map { insertions -> insertions.toInt() }) }
        }

        updatePostRequest.postTags
            ?.map { postTagService.findOrCreateByName(it) }?.toSet()
            ?.forEach { post.addPostTag(it) }

        post = postRepository.save(post)
        return post.toPostResponseDto()
    }

    private fun attachFilesToPost(files: Array<MultipartFile>, post: Post, attachmentInsertions: List<Int>) {
        files.forEachIndexed { index, multipartFile -> post.attachments.add(attachmentInsertions[index], fileService.createFile(multipartFile, post.blog)) }
    }
    private fun attachFilesToPost(files: Array<MultipartFile>, post: Post) {
        files.forEach { post.attachments.add(fileService.createFile(it, post.blog)) }
    }

    @Caching(
        evict = [CacheEvict(value = arrayOf(CACHE_NAME), key = "#post.id"), CacheEvict(
            value = arrayOf(CACHE_NAME_PAGE),
            allEntries = true
        )]
    )
    fun deletePost(post: Post) {
        checkOwnership(post)
        postRepository.delete(post)
    }

    fun deletePost(id: Long) {
        checkOwnership(getPost(id))
        postRepository.deleteById(id)
    }
    

    //    @Cacheable(value = CACHE_NAME_PAGE, key = "T(java.lang.String).valueOf(#page).concat('-').concat(#pageSize)")
    fun searchPosts(
        optionalBlogName: Optional<String>,
        optionalCategory: Optional<String>,
        optionalTagNames: Optional<List<String>>,
        pageRequest: PageRequest?
    ): Page<PostResponseDto> {
        val postSearch = PostSearch()
        if (optionalBlogName.isPresent) {
            val blogName = optionalBlogName.get()
            val blog = blogService.getBlog(blogName)
            postSearch.blog = blog
        }
        if (optionalTagNames.isPresent) {
            val tagNames = optionalTagNames.get()
            val postTags = postTagService.getTags(tagNames)
            postSearch.postTags = postTags
        }
        if (optionalCategory.isPresent) {
            val category = optionalCategory.get()
            postSearch.postCategory = PostCategory.valueOf(category)
        } else {
            postSearch.postCategory = PostCategory.ROOT
        }
        val postSpecification = PostSpecification(postSearch)
        val posts = postRepository.findAll(postSpecification, pageRequest)
        return posts.map { it.toPostResponseDto() }
    }

    @Cacheable(CACHE_NAME)
    fun getPost(postId: Long?): Post {
        val post = postRepository.findById(postId)
        return if (!post.isPresent) {
            throw NotFoundException("Post with id $postId is not found.")
        } else {
            if (!post.get().published) {
                checkOwnership(post.get())
            }
            post.get()
        }
    }

    fun getPostResponseDto(postId: Long?): PostResponseDto {
        val post = getPost(postId)
        return post.toPostResponseDto()
    }

    private fun checkOwnership(post: Post) {
        val isAnon =
            SecurityContextHolder.getContext().authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        if (isAnon) {
            throw AccessDeniedException("User is anonymous.")
        }
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        if (post.blog != userPrincipal.activeBlog && !userPrincipal.hasAuthority(UserAuthority.POST_MANAGE)) {
            throw AccessDeniedException("User does not own post")
        }
    }

    companion object {
        const val CACHE_NAME = "cache.posts"
        const val CACHE_NAME_PAGE = CACHE_NAME + ".page"
    }
}