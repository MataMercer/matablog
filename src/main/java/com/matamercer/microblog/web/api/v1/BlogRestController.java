package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.security.CurrentUser;
import com.matamercer.microblog.services.BlogService;
import com.matamercer.microblog.services.FollowService;
import com.matamercer.microblog.web.api.v1.dto.requests.FollowRequestDto;
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogRestController {
    private final BlogService blogService;
    private final FollowService followService;

    @Autowired
    public BlogRestController(BlogService blogService, FollowService followService) {
        this.blogService = blogService;
        this.followService = followService;
    }

    @GetMapping("/name/{blogName}")
    public ResponseEntity<Blog> getBlogByName(@PathVariable String blogName){
        return ResponseEntity.ok(blogService.getBlog(blogName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponseDto> getBlogById(@PathVariable String id){
        return ResponseEntity.ok(blogService.getBlogResponseDto(Long.parseLong(id)));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followBlog(@PathVariable String id, @Valid FollowRequestDto followRequestDTO, @CurrentUser User userPrincipal){
        followService.followBlog(userPrincipal.getActiveBlog(), Long.parseLong(id), followRequestDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<?> unfollowBlog(@PathVariable String id, @CurrentUser User userPrincipal){
        followService.unfollowBlog(userPrincipal.getActiveBlog(), Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
