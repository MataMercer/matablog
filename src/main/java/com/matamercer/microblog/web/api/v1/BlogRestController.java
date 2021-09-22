package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.services.BlogService;
import com.matamercer.microblog.web.api.v1.dto.requests.FollowRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogRestController {
    private final BlogService blogService;

    @Autowired
    public BlogRestController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/name/{blogName}")
    public ResponseEntity<Blog> getBlogByName(@PathVariable String blogName){
        return ResponseEntity.ok(blogService.getBlog(blogName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable String id){
        return ResponseEntity.ok(blogService.getBlog(Long.parseLong(id)));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followBlog(@PathVariable String id, Principal principal, @Valid FollowRequestDto followRequestDTO){
        blogService.followBlog(principal, Long.parseLong(id), followRequestDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<?> unfollowBlog(@PathVariable String id, Principal principal){
        blogService.unfollowBlog(principal, Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
