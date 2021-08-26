package com.matamercer.microblog.web;

import com.matamercer.microblog.models.entities.*;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.services.PostService;
import com.matamercer.microblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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



}
