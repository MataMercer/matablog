package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    PostTag findByName(String name);
}
