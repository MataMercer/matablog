package com.matamercer.microblog.web;

import com.matamercer.microblog.forms.CreatePostForm;
import com.matamercer.microblog.forms.RegisterUserForm;
import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.enums.PostCategory;
import com.matamercer.microblog.models.repositories.BlogRepository;
import com.matamercer.microblog.models.repositories.PostTagRepository;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.models.repositories.searches.PostSearch;
import com.matamercer.microblog.services.FileService;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.PostTagService;
import com.matamercer.microblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final BlogRepository blogRepository;
    private final PostService postService;
    private final PostTagService postTagService;
    private final FileService fileService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    private static final int PAGE_SIZE = 40;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder,
                          BlogRepository blogRepository, PostTagRepository postTagRepository, PostService postService,
                          PostTagService postTagService, FileService fileService, ApplicationEventPublisher applicationEventPublisher,
                          MessageSource messageSource) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.blogRepository = blogRepository;
        this.postService = postService;
        this.postTagService = postTagService;
        this.fileService = fileService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messageSource = messageSource;
    }


    @GetMapping("/login")
    public ModelAndView login(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey") final Optional<String> messageKey, @RequestParam("error") final Optional<String> error) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent(key -> {
                    String message = messageSource.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );

        error.ifPresent(e -> model.addAttribute("error", e));

        return new ModelAndView("login", model);
    }

    @GetMapping("register")
    public String getRegisterView(Model model) {
        RegisterUserForm registerUserForm = new RegisterUserForm();
        model.addAttribute(registerUserForm);
        return "register";
    }

    @GetMapping("registerSuccess")
    public String getRegisterSuccessView() {
        return "registerSuccess";
    }

    @GetMapping("registerOAuth2Failure")
    public String getRegisterOAuth2Failure() {
        return "registerOAuth2Failure";
    }

    @GetMapping("/registration/confirm")
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        Locale locale = request.getLocale();
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messageSource.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messageSource.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/baduser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("message.regSuccConfirmed", null, locale));
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String getHomeView() {
        return "home";
    }


    @GetMapping({"/profile/{blogName}","/profile/{blogName}/{category}"})
    public String getProfile(Model model,
                             @PathVariable(required = false) Optional<String> category,
                             @PathVariable String blogName,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) Optional<List<String>> postTagNames
    ) {
        Blog blog = blogRepository.findByBlogName(blogName);
        model.addAttribute("profileBlog", blog);

        PostSearch postSearch = new PostSearch();
        if(postTagNames.isPresent()) {
            Set<PostTag> postTags = postTagService.getTags(postTagNames.get());
            postSearch.setPostTags(postTags);
        }
        if(category.isPresent()) {
            postSearch.setPostCategory(PostCategory.valueOf(category.get().toUpperCase()));
        }else{
            postSearch.setPostCategory(PostCategory.ROOT);
        }
        postSearch.setBlog(blog);

        Page<Post> posts = postService.searchPosts(postSearch, page, PAGE_SIZE);

        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);

        if(category.isPresent()) {
            model.addAttribute("category", PostCategory.valueOf(category.get().toUpperCase()));
        }else{
            model.addAttribute("category", PostCategory.ROOT);
        }
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

    @GetMapping("/posts/{postId}")
    public String getPostPage(Model model, @PathVariable("postId") long postId) {
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
