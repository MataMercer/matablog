package com.matamercer.microblog.services;


import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Page<Post> getAllPostsByPageSortedByCreatedAt(int page, int pageSize){
        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
    }
}
