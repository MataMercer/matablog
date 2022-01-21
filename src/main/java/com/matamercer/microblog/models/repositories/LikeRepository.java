package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Like;
import com.matamercer.microblog.models.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByPost_IdAndLiker(long postId, Blog liker);
    Long countLikesByPost(Post post);
}
