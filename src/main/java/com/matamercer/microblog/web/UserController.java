package com.matamercer.microblog.web;

import com.matamercer.microblog.Exceptions.NotFoundException;
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
import com.matamercer.microblog.utilities.AuthenticationResponse;
import com.matamercer.microblog.web.error.UserNotFoundException;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PostService postService;
    private final MessageSource messageSource;

    private static final int PAGE_SIZE = 40;

    @Autowired
    public UserController(UserRepository userRepository,
                          UserService userService,
                          PostService postService,
                          MessageSource messageSource) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.postService = postService;
        this.messageSource = messageSource;
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

    @GetMapping("/currentuser")
    public ResponseEntity<?> currentUser(HttpServletRequest request) {
        var optionalUser = userRepository.findByUsername(request.getUserPrincipal().getName());
        if(!optionalUser.isPresent()){
           throw new UserNotFoundException("Unable to find current user.");
        }
        User user = optionalUser.get();
        return ResponseEntity.ok(user.getEmail());
    }



}
