package com.matamercer.microblog.controllers;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    private static final int PAGE_SIZE = 20;

    @GetMapping("login")
    public String getLoginView(){
        return "login";
    }

    @GetMapping("home")
    public String getHomeView(){
        return "home";
    }

    @GetMapping("{username}")
    public String getProfile(Model model, @PathVariable("username") String username, @RequestParam(defaultValue = "0") int page){

        User user = userRepository.findByUsername(username);
        model.addAttribute("profileUser", user);

        Page<Post> posts = postService.getAllPostsByPageSortedByCreatedAt(page, PAGE_SIZE);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("posts", posts);

        return "profile";
    }

    @GetMapping("/newpost")
    public String createPostForm(Model model){
        CreatePostForm createPostForm = new CreatePostForm();
        model.addAttribute("createPostForm", createPostForm);
        return "createPostForm";
    }

    @PostMapping("/newpost")
    public String createPostForm(Model model, @Valid CreatePostForm createPostForm, Principal principal){
        Post post = new Post();
        post.setContent(createPostForm.getContent());
        post.setUser(userRepository.findByUsername(principal.getName()));
        postService.createPost(post);
        return "redirect:/";
    }

    @GetMapping("admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String getAdminDashboardView(){
        return "adminDashboard";
    }

}
