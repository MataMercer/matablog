package com.matamercer.microblog.unit.service;


import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.security.authorization.UserRole;
import com.matamercer.microblog.services.BlogService;
import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import com.matamercer.microblog.web.api.v1.dto.mappers.request.PostRequestDtoMapper;
import com.matamercer.microblog.web.api.v1.dto.mappers.response.PostResponseDtoMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ActiveProfiles("Test")
public class PostServiceTest {

   private PostService postService;
   private Post post;
   private User user;

   @Before
   public void setup(){

      post = new Post(Mockito.mock(Blog.class),
              "title",
              "content",
              false,
              false, true);
      post.setId(1L);

      PostRepository postRepository = Mockito.mock(PostRepository.class);
      Mockito.when(postRepository.save(any(Post.class)))
              .thenAnswer(i -> i.getArguments()[0]);

      PostTagService postTagService = Mockito.mock(PostTagService.class);

      BlogService blogService = Mockito.mock(BlogService.class);

      FileService fileService = Mockito.mock(FileService.class);

      PostResponseDtoMapper postResponseDtoMapper = Mockito.mock(PostResponseDtoMapper.class);
      PostRequestDtoMapper postRequestDtoMapper = Mockito.mock(PostRequestDtoMapper.class);

      user = new User("username@gmail.com",
              "username",
              "password",
              UserRole.BLOGGER,
              true,
              true,
              true,
              true,
              AuthenticationProvider.LOCAL);
      postService = new PostService(postRepository,
              postTagService,
              blogService,
              fileService,
              postResponseDtoMapper,
              postRequestDtoMapper);
   }

//   @Test
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
