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
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles("Test")
class PostServiceTest {
    private var postService: PostService? = null
    private var post: Post? = null
    private var user: User? = null
    @Before
    fun setup() {
        post = Post(
            Mockito.mock(Blog::class.java),
            "title",
            "content",
            false,
            false, true
        )
        post!!.id = 1L
        val postRepository: PostRepository = Mockito.mock<PostRepository>(PostRepository::class.java)
        Mockito.`when`<Post>(postRepository.save<Post>(ArgumentMatchers.any<Post>(Post::class.java)))
            .thenAnswer(Answer<Any> { i: InvocationOnMock -> i.getArguments().get(0) })
        val postTagService: PostTagService = Mockito.mock<PostTagService>(PostTagService::class.java)
        val blogService: BlogService = Mockito.mock<BlogService>(BlogService::class.java)
        val fileService: FileService = Mockito.mock<FileService>(FileService::class.java)
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
    } //   @Test
    //   public void whenCreatePost_returnCreatedPost(){
    //      PostRequestDto postRequestDto = new PostRequestDto();
    //      postRequestDto.setContent(post.getContent());
    //      postRequestDto.setTitle(post.getTitle());
    //      MultipartFile[] files = new MultipartFile[0];
    //      Post createdPost = postService.createNewPost(postRequestDto, files, user.getActiveBlog());
    //      assertThat(createdPost.getContent()).isEqualTo(postRequestDto.getContent());
    //      assertThat(createdPost.getTitle()).isEqualTo(postRequestDto.getTitle());
    //   }
    //
    //   @Test
    //   public void whenConvertPost_returnResponseDto(){
    //      PostRequestDto postRequestDto = new PostRequestDto();
    //      postRequestDto.setContent(post.getContent());
    //      postRequestDto.setTitle(post.getTitle());
    //      MultipartFile[] files = new MultipartFile[0];
    //      Post createdPost = postService.createNewPost(postRequestDto, files, user.getActiveBlog());
    //
    //      PostResponseDto postResponseDto = postService.convertEntityToDtoResponse(createdPost);
    //      assertThat(postResponseDto.getContent()).isEqualTo(createdPost.getContent());
    //   }
}