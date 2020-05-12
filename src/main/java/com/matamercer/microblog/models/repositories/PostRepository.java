package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUser(User user, Pageable pageRequest);
}
