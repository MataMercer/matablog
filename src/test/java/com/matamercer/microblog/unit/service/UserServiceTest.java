package com.matamercer.microblog.unit.service;


import com.google.common.collect.Sets;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.web.api.v1.forms.CreatePostForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UserServiceTest {

   private PostService postService;
   private Post post;

   @Before
   public void setup(){

      post = new Post(Mockito.mock(Blog.class),
              "title",
              "content",
              false,
              false);
      post.setId(1L);

      PostRepository postRepository = Mockito.mock(PostRepository.class);
      Mockito.when(postRepository.save(post)).thenReturn(post);

      PostTagService postTagService = Mockito.mock(PostTagService.class);
      Mockito.when(postTagService.findOrCreateByName("postTagName")).thenReturn(new PostTag("postTagName"));

      UserRepository userRepository = Mockito.mock(UserRepository.class);

      BlogRepository blogRepository = Mockito.mock(BlogRepository.class);

      FileService fileService = Mockito.mock(FileService.class);

      postService = new PostService(postRepository,
              postTagService,
              userRepository,
              blogRepository,
              fileService);
   }

//   @Test
//   public void whenCreatePost_returnCreatedPost(){
//      CreatePostForm createPostForm = new CreatePostForm();
//      createPostForm.setContent();
//      postService.createPost();
//
//   }
}
