package com.matamercer.microblog.unit.repository;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Like;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.repositories.LikeRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LikeRepositoryTest {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Blog blog;

    private Post post;

    private Set<Like> likes = new HashSet<>();

    @Test
    public void whenLikePost_returnPostLikeCount(){
        blog = new Blog("blogName", "preferredBlogName", false);
        blog = entityManager.persist(blog);

        post = new Post(blog, "title", "content", false, false, true);
        post = entityManager.persist(post);

        Like like = new Like(blog, post);
        like = entityManager.persist(like);
        likes.add(like);

        long count = likeRepository.countLikesByPost(post);
       assertThat(count).isEqualTo(likes.size());
    }



}
