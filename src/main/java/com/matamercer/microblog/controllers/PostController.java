package com.matamercer.microblog.controllers;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.models.entities.Post;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping(value = "posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    private static final int PAGE_SIZE = 20;

    @GetMapping("/")
    public String allPostsByPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Post> posts = postService.getAllPostsByPageSortedByCreatedAt(page, PAGE_SIZE);
        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("posts", posts);

        return "posts";
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
        return "redirect:/posts/";
    }

}
