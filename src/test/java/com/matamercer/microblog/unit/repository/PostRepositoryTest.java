package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.repositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void whenFindByBlog_returnBlog(){
        Blog blog = new Blog("blogName", "preferredBlogName", false);
        entityManager.persist(blog);

        Post post = new Post(blog, "title", "content", false, false);
        entityManager.persist(post);

        int page = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        Page<Post> postPage =  postRepository.findByBlog(blog, pageRequest);
        List<Post> postList= postPage.get().collect(Collectors.toList());
        Post foundPost = postList.get(0);

        assertThat(foundPost).isEqualTo(post);
    }
}
