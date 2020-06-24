package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.PostTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostTagService {

    @Autowired
    private PostTagRepository postTagRepository;

    public PostTag findOrCreateByName(String name) {
        PostTag postTag = postTagRepository.findByName(name);
        if (postTag == null) {
            postTag = postTagRepository.save(new PostTag(name));
        }
        return postTag;
    }

    public PostTag getTag(String name) {
        return postTagRepository.findByName(name);
    }

    public void deleteTag(PostTag tag) {
        postTagRepository.delete(tag);
    }
}
