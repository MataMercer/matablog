package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.security.CurrentUser;
import com.matamercer.microblog.services.LikeService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto;
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/post")
public class PostRestController {

    private final PostService postService;
    private final LikeService likeService;

    @Autowired
    public PostRestController(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPostResponseDto(Long.parseLong(id)));
    }

    @GetMapping("/")
    public ResponseEntity<Page<PostResponseDto>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int pageSize,
                                                          @RequestParam(name = "blogName", required = false) Optional<String> optionalBlogName,
                                                          @RequestParam(name = "category",required = false) Optional<String> optionalCategory,
                                                          @RequestParam(name = "tags", required = false) Optional<List<String>> optionalTagNames) {

        Page<PostResponseDto> posts = postService.searchPosts(optionalBlogName, optionalCategory, optionalTagNames, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(posts);
    }

//    @GetMapping("/{id}/replies")
//    public ResponseEntity<Page<PostResponseDto>> getPostReplies(@PathVariable String id) {
//        return ResponseEntity.ok(postService.getPostResponseDto(Long.parseLong(id)).getReplies());
//    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<URI>> getFileAttachments(@PathVariable String id) {
        assert id != null;
        val postAttachmentList = postService.getPost(Long.parseLong(id)).getAttachments();
        val postAttachmentURIList = postAttachmentList.stream().map(postAttachment -> linkTo(methodOn(FileRestController.class).serveFile(postAttachment.getId(), postAttachment.getName())).toUri()).collect(Collectors.toList());
        return ResponseEntity.ok(postAttachmentURIList);
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponseDto> createNewPostForm(@Valid PostRequestDto postRequestDto,
                                               @RequestParam(name="files", required = false) MultipartFile[] files, @CurrentUser User userPrincipal) {
        val post = postService.createNewPost(postRequestDto, files, userPrincipal.getActiveBlog());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }

    @PostMapping("/reply")
    public ResponseEntity<PostResponseDto> createReplyPostForm(@Valid PostRequestDto postRequestDto,
                                               @RequestParam(name="files", required = false) MultipartFile[] files, @CurrentUser User userPrincipal) {
        val post = postService.createReplyPost(postRequestDto, files, userPrincipal.getActiveBlog());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }
    
    @PutMapping("/update")
    public ResponseEntity<PostResponseDto> updatePostForm(@Valid PostRequestDto updatePostRequest, @RequestParam(name = "files", required = false) MultipartFile[] files, @CurrentUser User userPrincipal){
        val post = postService.updatePost(updatePostRequest, files, userPrincipal.getActiveBlog());
        return ResponseEntity.ok().body(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, @CurrentUser User userPrincipal){
        postService.deletePost(Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id, @CurrentUser User userPrincipal){
        likeService.likePost(userPrincipal.getActiveBlog(), Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable String id, @CurrentUser User userPrincipal){
        likeService.unlikePost(userPrincipal.getActiveBlog(), Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
