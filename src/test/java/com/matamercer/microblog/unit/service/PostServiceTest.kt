package com.matamercer.microblog.unit.service

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.PostRepository
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.BlogService
import com.matamercer.microblog.services.FileService
import com.matamercer.microblog.services.PostService
import com.matamercer.microblog.services.PostTagService
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.multipart.MultipartFile

@ExtendWith(MockKExtension::class)
@ActiveProfiles("Test")
class PostServiceTest {
    @MockK
    private lateinit var postService: PostService

    @MockK
    private lateinit var postRepository: PostRepository

    @MockK
    private lateinit var postTagService: PostTagService

    @MockK
    private lateinit var blogService: BlogService

    @MockK
    private lateinit var fileService: FileService
    private lateinit var post: Post
    private var user: User? = null

    @BeforeEach
    fun setup() {
        clearAllMocks()
        post = Post(
            mockkClass(Blog::class),
            "title",
            "content",
            false,
            false, true
        )
        post.id = 1L
        user = User(
            "username@gmail.com",
            "username",
            "password",
            UserRole.BLOGGER,
            AuthenticationProvider.LOCAL
        )
        postService = PostService(
            postRepository,
            postTagService,
            blogService,
            fileService,
        )
    }

    @Test
    fun whenCreatePost_returnCreatedPost() {
        val postRequestDto = PostRequestDto(sensitive = true)
        postRequestDto.content = post.content
        postRequestDto.title = post.title
        val blog = Blog()
        blog.id = 1L
        post.blog = blog
        val files = emptyArray<MultipartFile>()
        every { postRepository.save(any()) } returns post
        val createdPost = postService.createNewPost(postRequestDto, files, blog)
        assertThat(createdPost.content).isEqualTo(postRequestDto.content)
        assertThat(createdPost.title).isEqualTo(postRequestDto.title)
    }
 }