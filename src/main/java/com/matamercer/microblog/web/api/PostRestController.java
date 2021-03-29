package com.matamercer.microblog.web.api;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostRestController {

    private final PostService postService;
    private final FileService fileService;
    private final PostTagService postTagService;
    private final UserRepository userRepository;

    @Autowired
    public PostRestController(PostService postService, FileService fileService, PostTagService postTagService, UserRepository userRepository) {
        this.postService = postService;
        this.fileService = fileService;
        this.postTagService = postTagService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id){
        return ResponseEntity.ok(postService.getPost(Long.parseLong(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPostForm(@Valid CreatePostForm createPostForm,
                                         @RequestParam("files") MultipartFile[] files, Principal principal) {
        Set<PostTag> postTags = createPostForm.getPostTags().stream()
                .map(postTagService::findOrCreateByName).collect(Collectors.toSet());

        Blog blog = userRepository.findByUsername(principal.getName()).getActiveBlog();
        Post post = new Post(blog, createPostForm.getTitle(), createPostForm.getContent(),
                createPostForm.isCommunityTaggingEnabled(), createPostForm.isSensitive());

        attachFilesToPost(files, post);

        postTags.forEach(post::addPostTag);
        post = postService.createPost(post);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }

    private void attachFilesToPost(MultipartFile[] files, Post post) {
        for (MultipartFile file : files) {
            post.getAttachments().add(fileService.createFile(file, post.getBlog()));
        }
    }


}
