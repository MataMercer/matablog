package com.matamercer.microblog.web.api.v1;

import com.matamercer.microblog.models.entities.File;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.services.BlogService;
import com.matamercer.microblog.services.LikeService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto;
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final LikeService likeService;
    private final BlogService blogService;
    private final UserService userService;

    @Autowired
    public PostRestController(PostService postService, LikeService likeService, BlogService blogService, ModelMapper modelMapper, UserService userService) {
        this.postService = postService;
        this.likeService = likeService;
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPost(Long.parseLong(id)));
    }

    @GetMapping("/")
    public ResponseEntity<Page<PostResponseDto>> getPosts(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int pageSize,
                                                          @RequestParam(name = "blog", required = false) Optional<String> optionalBlogName,
                                                          @RequestParam(name = "category",required = false) Optional<String> optionalCategory,
                                                          @RequestParam(name = "tags", required = false) Optional<List<String>> optionalTagNames) {

        Page<Post> posts = postService.searchPosts(optionalBlogName, optionalCategory, optionalTagNames, PageRequest.of(page, pageSize, Sort.Direction.DESC, "createdAt"));
        Page<PostResponseDto> dtoPosts = new PageImpl<PostResponseDto>(posts.getContent().stream().map(postService::convertEntityToDtoResponse).collect(Collectors.toList()));
        return ResponseEntity.ok(dtoPosts);
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
    public ResponseEntity<Post> createPostForm(@Valid PostRequestDto postRequestDto,
                                               @RequestParam(name="files", required = false) MultipartFile[] files, Principal principal) {
        User user = userService.getUser(principal);
        Post post = postService.createPost(postRequestDto, files, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }
    
//    @PutMapping("/update")
//    public ResponseEntity<Post> updatePostForm(@Valid PostRequestDto updatePostRequest, @RequestParam(name = "files", required = false) MultipartFile[] files, Principal principal){
//        Post post = postService.updatePost(updatePostRequest, files, principal);
//        return ResponseEntity.ok().body(post);
//    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id,Principal principal){
        likeService.likePost(blogService.getActiveBlog(principal), Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable String id,Principal principal){
        likeService.unlikePost(blogService.getActiveBlog(principal), Long.parseLong(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
