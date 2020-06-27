package com.matamercer.microblog.Integration;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.PostTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class PostTagRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostTagRepository postTagRepository;

    @Test
    public void whenFindByBlogSortedByMostUsed_thenReturnListOfPostTags() throws Exception{
        String testTagName1 = "tag1";
        PostTag pt1 = new PostTag(testTagName1);
        pt1 = entityManager.persist(new PostTag(testTagName1));

        String testTagName2 = "tag2";
        PostTag pt2 = new PostTag(testTagName2);
        pt2 = entityManager.persist(new PostTag(testTagName2));

        Blog blog = new Blog();
        blog.setBlogname("");
        blog.setPreferredBlogName("");
        blog.setSensitive(false);
        blog = entityManager.persist(blog);

        Post post = new Post();
        post.setContent("");
        post.setTitle("");
        post.setSensitive(false);
        post.setCommunityTaggingEnabled(false);
        post.setBlog(blog);
        post = entityManager.persist(post);
        post.addPostTag(pt1);


        Post post2 = new Post();
        post2.setContent("");
        post2.setTitle("");
        post2.setSensitive(false);
        post2.setCommunityTaggingEnabled(false);
        post2.setBlog(blog);
        post2 = entityManager.persist(post2);
        post2.addPostTag(pt1);
        post2.addPostTag(pt2);

        int page = 0;
        int pageSize = 10;
//        Map<PostTag, Integer> postTagIntegerMap = postTagRepository.findByBlogSortedByMostUsedMap(blog, PageRequest.of(page, pageSize));
//        assertThat(postTagIntegerMap.get(pt1)).isEqualTo(2);
        Map<PostTag, Integer> postTagPage = postTagRepository.findByBlogSortedByMostUsedMap(blog, PageRequest.of(page, pageSize, Sort.Direction.ASC, "postTagCount"));
        assertThat(postTagPage.get(pt1)).isEqualTo(2);
        assertThat(postTagPage.get(pt2)).isEqualTo(1);

        pageSize=1;
        Map<PostTag, Integer> postTagPage2 = postTagRepository.findByBlogSortedByMostUsedMap(blog, PageRequest.of(page, pageSize));
        assertThat(postTagPage2.get(pt2)).isEqualTo(null);
    }


}
