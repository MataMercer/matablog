package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
