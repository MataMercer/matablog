package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.enums.PostCategory;
import com.matamercer.microblog.models.repositories.PostRepository;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import com.matamercer.microblog.models.repositories.specifications.PostSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

//    @Test
//    public void whenFindByBlog_returnBlog(){
//        Blog blog = new Blog("blogName", "preferredBlogName", false);
//        entityManager.persist(blog);
//
//        Post post = new Post(blog, "title", "content", false, false);
//        entityManager.persist(post);
//
//        int page = 0;
//        int pageSize = 10;
//        PageRequest pageRequest = PageRequest.of(page, pageSize);
//
//        Page<Post> postPage =  postRepository.findByBlog(blog, pageRequest);
//        List<Post> postList= postPage.get().collect(Collectors.toList());
//        Post foundPost = postList.get(0);
//
//        assertThat(foundPost).isEqualTo(post);
//    }

    @Test
    public void whenSearchByBlog_ReturnFilteredPosts(){
        Blog blog = new Blog("blogName", "preferredBlogName", false);
        entityManager.persist(blog);

        Post post = new Post(blog, "title", "content", false, false, true);
        entityManager.persist(post);

        int page = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        PostSearch postSearch = new PostSearch();
        postSearch.setBlog(blog);


        PostSpecification postSpecification = new PostSpecification(postSearch);

        Page<Post> postPage =  postRepository.findAll(postSpecification, pageRequest);
        List<Post> postList= postPage.get().collect(Collectors.toList());
        Post foundPost = postList.get(0);

        assertThat(foundPost).isEqualTo(post);
    }

    @Test
    public void whenSearchByTag_ReturnFilteredPosts(){
        Blog blog = new Blog("blogName", "preferredBlogName", false);
        entityManager.persist(blog);

        Post post = new Post(blog, "title", "content", false, false, true);
        entityManager.persist(post);


        Post post2 = new Post(blog, "title", "content", false, false, true);
        entityManager.persist(post2);

        PostTag postTag = new PostTag("postTag1");
        entityManager.persist(postTag);


        PostTag postTag2 = new PostTag("postTag2");
        entityManager.persist(postTag2);

        post.addPostTag(postTag);
        post2.addPostTag(postTag);
        post2.addPostTag(postTag2);
        entityManager.persist(post);
        entityManager.persist(post2);

        int page = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        PostSearch postSearch = new PostSearch();
        postSearch.setBlog(blog);
        postSearch.setPostTags(Set.of(postTag, postTag2));
        postSearch.setPostCategory(PostCategory.ROOT);

        PostSpecification postSpecification = new PostSpecification(postSearch);

        Page<Post> postPage =  postRepository.findAll(postSpecification, pageRequest);
        List<Post> postList= postPage.get().collect(Collectors.toList());
        Post foundPost = postList.get(0);

        assertThat(foundPost).isEqualTo(post2);
        assertThat(postList.size()).isEqualTo(1);
    }
}
