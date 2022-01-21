package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Like;
import com.matamercer.microblog.models.repositories.LikeRepository;
import com.matamercer.microblog.web.api.v1.dto.responses.LikeResponseDto;
import com.matamercer.microblog.web.error.exceptions.AlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostService postService;

    public LikeService(LikeRepository likeRepository, PostService postService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
    }

    public void likePost(Blog blog, Long postId){
        Like existingLike =findByPostIdAndLiker(postId, blog);
        if(existingLike != null){
            throw new AlreadyExistsException();
        }

        Like like = new Like(blog, postService.getPost(postId));
        likeRepository.save(like);
    }

    public void unlikePost(Blog blog, long postId){
        likeRepository.delete(findByPostIdAndLiker(postId, blog));
    }

    public Like findByPostIdAndLiker(long postId, Blog blog){
        return likeRepository.findByPost_IdAndLiker(postId ,blog);
    }
}
