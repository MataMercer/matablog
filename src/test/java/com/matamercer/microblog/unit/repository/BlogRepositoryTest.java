package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.repositories.BlogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class BlogRepositoryTest {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void whenFindByBlogName_returnBlog(){
        Blog blog = new Blog("BlogName", "PreferredBlogName", false);
        blog = entityManager.persist(blog);
        Blog foundBlog = blogRepository.findByBlogName(blog.getBlogName());
        assertThat(foundBlog).isEqualTo(blog);
    }
}
