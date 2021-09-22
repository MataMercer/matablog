package com.matamercer.microblog.services;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Like;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.repositories.LikeRepository;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostService postService;

    public LikeService(LikeRepository likeRepository, PostService postService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
    }

    public void likePost(Blog blog, Long id){
        Like like = new Like(blog, postService.getPost(id));
        likeRepository.save(like);
    }

    public void unlikePost(Blog blog, long id){
        Like like = likeRepository.findByPost(postService.getPost(id));
        likeRepository.delete(like);
    }

    public long countLikes(Post post){
        return likeRepository.countLikesByPost(post);
    }


}
