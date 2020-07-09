package com.matamercer.microblog.controllers;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostTagRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final PostService postService;
    private final PostTagService postTagService;
    private final FileService fileService;

    private static final int PAGE_SIZE = 40;

    @Autowired
    public UserController(UserRepository userRepository, BlogRepository blogRepository, PostTagRepository postTagRepository, PostService postService, PostTagService postTagService, FileService fileService) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.postService = postService;
        this.postTagService = postTagService;
        this.fileService = fileService;
    }

    @GetMapping("/login")
    public String getLoginView() {

        return "login";
    }

    @GetMapping("/home")
    public String getHomeView() {

        return "home";
    }

    @GetMapping("/profile/{blogname}")
    public String getProfile(Model model, @PathVariable("blogname") String blogname,
                             @RequestParam(defaultValue = "0") int page) {

        Blog blog = blogRepository.findByBlogname(blogname);
        model.addAttribute("profileBlog", blog);

        Page<Post> posts = postService.getAllPostsByPageByBlogSortedByCreated(blog, page, PAGE_SIZE);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("posts", posts.toList());

        model.addAttribute("mostUsedTags", postTagService.getTopTagsByPosts(blog, 0, 10));

        return "profile";
    }

    @GetMapping("/newpost")
    public String createPostForm(Model model) {
        CreatePostForm createPostForm = new CreatePostForm();
        model.addAttribute("createPostForm", createPostForm);
        return "createPostForm";
    }

    @PostMapping("/newpost")
    public String createPostForm(@Valid CreatePostForm createPostForm, Errors errors, @RequestParam("file") MultipartFile file, Model model, Principal principal ) {

        if(errors.hasErrors()){
            return "createPostForm";
        }

        Set<PostTag> postTags = createPostForm.getPostTags().stream().map(postTagName -> postTagService.findOrCreateByName(postTagName)).collect(Collectors.toSet());


        Blog blog = userRepository.findByUsername(principal.getName()).getActiveBlog();
        Post post = new Post(
                blog,
                createPostForm.getTitle(),
                createPostForm.getContent(),
                createPostForm.isCommunityTaggingEnabled(),
                createPostForm.isSensitive());

        if(!file.isEmpty()){
            post.getAttachments().add(fileService.createFile(file, blog));
        }

        postTags.forEach(post::addPostTag);
        post = postService.createPost(post);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{postId}")
    public String getPostPage(Model model, @PathVariable("postId") long postId){
        Post post = postService.getPost(postId);
        model.addAttribute("post", post);

        return "post";
    }

    @GetMapping("admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String getAdminDashboardView() {
        return "adminDashboard";
    }

}
