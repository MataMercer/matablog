package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.web.api.v1.forms.CreatePostForm;
import com.matamercer.microblog.web.api.v1.forms.UpdatePostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/post")
public class PostRestController {

    private final PostService postService;

    @Autowired
    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPost(Long.parseLong(id)));
    }

    @GetMapping("/")
    public ResponseEntity<Page<Post>> getPosts(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int pageSize,
                                               @RequestParam(required = false) Optional<String> optionalBlogName,
                                               @RequestParam(required = false) Optional<String> optionalCategory,
                                               @RequestParam(required = false) Optional<List<String>> optionalTagNames) {

        Page<Post> posts = postService.searchPosts(optionalBlogName, optionalCategory, optionalTagNames, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Post>> getPostReplies(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPost(Long.parseLong(id)).getReplies());
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<URI>> getFileAttachments(@PathVariable String id) {
        assert id != null;
        List<File> postAttachmentList = postService.getPost(Long.parseLong(id)).getAttachments();
        List<URI> postAttachmentURIList = postAttachmentList.stream().map(postAttachment -> linkTo(methodOn(FileRestController.class).serveFile(postAttachment.getId(), postAttachment.getName())).toUri()).collect(Collectors.toList());
        return ResponseEntity.ok(postAttachmentURIList);
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPostForm(@Valid CreatePostForm createPostForm,
                                               @RequestParam(name="files", required = false) MultipartFile[] files, Principal principal) {
        Post post = postService.createPost(createPostForm, files, principal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }
    
    @PutMapping("/update")
    public ResponseEntity<Post> updatePostForm(@Valid UpdatePostForm updatePostForm, @RequestParam(name = "files", required = false) MultipartFile[] files, Principal principal){
        Post post = postService.updatePost(updatePostForm, files, principal);
        return ResponseEntity.ok().body(post);
    }
}
