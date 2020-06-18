package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findByBlogname(String blogname);
}
