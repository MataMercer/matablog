package com.matamercer.microblog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "posts")
public class PostController {


//    @GetMapping("/")
//    public String allPostsByPage(@RequestParam(defaultValue = "0") int page, Model model) {
//        Page<Post> posts = postService.getAllPostsByPageSortedByCreatedAt(page, PAGE_SIZE);
//        model.addAttribute("totalPages", posts.getTotalPages());
//        model.addAttribute("page", page);
//        model.addAttribute("posts", posts);
//
//        return "posts";
//    }
//@GetMapping("/home")
//public String getHomeView(){
//    return "home";
//}
//
//    @GetMapping("/newpost")
//    public String createPostForm(Model model){
//        CreatePostForm createPostForm = new CreatePostForm();
//        model.addAttribute("createPostForm", createPostForm);
//        return "createPostForm";
//    }
//
//    @PostMapping("/newpost")
//    public String createPostForm(Model model, @Valid CreatePostForm createPostForm, Principal principal){
//        Post post = new Post();
//        post.setContent(createPostForm.getContent());
//        post.setUser(userRepository.findByUsername(principal.getName()));
//        postService.createPost(post);
//        return "redirect:/posts/";
//    }

}
