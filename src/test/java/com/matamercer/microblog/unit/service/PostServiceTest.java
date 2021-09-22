package com.matamercer.microblog.unit.service;


import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto;
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;


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

      BlogRepository blogRepository = Mockito.mock(BlogRepository.class);

      FileService fileService = Mockito.mock(FileService.class);

      user = new User("username@gmail.com",
              "username",
              "password",
              UserRole.USER,
              true,
              true,
              true,
              true,
              AuthenticationProvider.LOCAL);
      postService = new PostService(postRepository,
              postTagService,
              blogRepository,
              fileService,
              new ModelMapper());
   }

   @Test
   public void whenCreatePost_returnCreatedPost(){
      PostRequestDto postRequestDto = new PostRequestDto();
      postRequestDto.setContent(post.getContent());
      postRequestDto.setTitle(post.getTitle());
      MultipartFile[] files = new MultipartFile[0];
      Post createdPost = postService.createPost(postRequestDto, files, user);
      assertThat(createdPost.getContent()).isEqualTo(postRequestDto.getContent());
      assertThat(createdPost.getTitle()).isEqualTo(postRequestDto.getTitle());
   }

   @Test
   public void whenConvertPost_returnResponseDto(){
      PostRequestDto postRequestDto = new PostRequestDto();
      postRequestDto.setContent(post.getContent());
      postRequestDto.setTitle(post.getTitle());
      MultipartFile[] files = new MultipartFile[0];
      Post createdPost = postService.createPost(postRequestDto, files, user);

      PostResponseDto postResponseDto = postService.convertEntityToDtoResponse(createdPost);
      assertThat(postResponseDto.getContent()).isEqualTo(createdPost.getContent());
   }


}
