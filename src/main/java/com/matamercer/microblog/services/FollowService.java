package com.matamercer.microblog.services;

import com.matamercer.microblog.models.repositories.FollowRepository;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }
}
