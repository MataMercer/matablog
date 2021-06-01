package com.matamercer.microblog.web.api;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.web.FileController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/post")
public class PostRestController {

    private final PostService postService;

    @Autowired
    public PostRestController(PostService postService ) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id){
        return ResponseEntity.ok(postService.getPost(Long.parseLong(id)));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Post>> getPostReplies(@PathVariable String id){
        return ResponseEntity.ok(postService.getPost(Long.parseLong(id)).getReplies());
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<URI>>getFileAttachments(@PathVariable String id){
        assert id != null;
        List<File> postAttachmentList = postService.getPost(Long.parseLong(id)).getAttachments();
        List<URI> postAttachmentURIList = postAttachmentList.stream().map(postAttachment -> linkTo(methodOn(FileController.class).serveFile(postAttachment.getId(), postAttachment.getName())).toUri()).collect(Collectors.toList());
        return ResponseEntity.ok(postAttachmentURIList);
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPostForm(@Valid CreatePostForm createPostForm,
                                         @RequestParam("files") MultipartFile[] files, Principal principal) {
        Post post = postService.createPost(createPostForm, files, principal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }
}
