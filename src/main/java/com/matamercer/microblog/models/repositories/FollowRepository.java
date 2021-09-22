package com.matamercer.microblog.models.repositories;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowerAndFollowee(Blog follower, Blog followee);
}
