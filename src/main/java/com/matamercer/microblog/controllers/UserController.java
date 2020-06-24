package com.matamercer.microblog.controllers;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.Blog;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.PostTag;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostTagRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostTagService postTagService;

    private static final int PAGE_SIZE = 40;

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

        return "profile";
    }

    @GetMapping("/newpost")
    public String createPostForm(Model model) {
        CreatePostForm createPostForm = new CreatePostForm();
        model.addAttribute("createPostForm", createPostForm);
        return "createPostForm";
    }

    @PostMapping("/newpost")
    public String createPostForm(Model model, @Valid CreatePostForm createPostForm, Principal principal) {

        Set<PostTag> postTags = createPostForm.getPostTags().stream().map(postTagName -> postTagService.findOrCreateByName(postTagName)).collect(Collectors.toSet());


        Post post = new Post(userRepository.findByUsername(principal.getName()).getActiveBlog(),
                createPostForm.getTitle(),
                createPostForm.getContent(),
                createPostForm.isCommunityTaggingEnabled(),
                createPostForm.isSensitive());

        postTags.forEach(post::addPostTag);
        postService.createPost(post);
        return "redirect:/";
    }

    @GetMapping("admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String getAdminDashboardView() {
        return "adminDashboard";
    }

}
