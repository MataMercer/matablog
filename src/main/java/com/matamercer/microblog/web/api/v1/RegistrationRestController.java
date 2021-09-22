package com.matamercer.microblog.web.api.v1;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.matamercer.microblog.services.BlogService;
import com.matamercer.microblog.web.api.v1.dto.requests.RegisterUserRequestDto;
import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.VerificationToken;
import com.matamercer.microblog.security.OnRegistrationCompleteEvent;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.utilities.GenericResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationRestController {
  private final UserService userService;
  private final BlogService blogService;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final MessageSource messageSource;
  private final JavaMailSender mailSender;

  @Autowired
  public RegistrationRestController(UserService userService,
                                    BlogService blogService, PasswordEncoder passwordEncoder, ApplicationEventPublisher applicationEventPublisher, MessageSource messageSource,
                                    JavaMailSender mailSender) {
    this.userService = userService;
    this.blogService = blogService;
    this.passwordEncoder = passwordEncoder;
    this.applicationEventPublisher = applicationEventPublisher;
    this.messageSource = messageSource;
    this.mailSender = mailSender;
  }

  @PostMapping("/createAccount")
  public GenericResponse registerUserAccount(@RequestBody @Valid RegisterUserRequestDto registerUserRequestDTO, HttpServletRequest request) {
    User registeredUser = new User(
            registerUserRequestDTO.getEmail(),
            registerUserRequestDTO.getUsername(),
            passwordEncoder.encode(registerUserRequestDTO.getPassword()),
            UserRole.USER,
            true,
            true,
            true,
            false,
            AuthenticationProvider.LOCAL);
    registeredUser = userService.createUser(registeredUser);
    blogService.createDefaultBlogForUser(registeredUser);

    applicationEventPublisher
        .publishEvent(new OnRegistrationCompleteEvent(registeredUser, request.getLocale(), request.getContextPath()));

    return new GenericResponse("success");
  }

  @GetMapping("/resendRegistrationToken")
  public GenericResponse resendRegistrationToken(HttpServletRequest request,
      @RequestParam("token") String existingToken) {
    VerificationToken newToken = userService.generateNewVerificationToken(existingToken);

    User user = userService.getUser(newToken);
    String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    SimpleMailMessage email = constructResendVerificationTokenEmail(appUrl, request.getLocale(), newToken, user);
    mailSender.send(email);

    return new GenericResponse(messageSource.getMessage("message.resendToken", null, request.getLocale()));
  }

  // Helper Methods
  private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale,
      final VerificationToken newToken, final User user) {
    final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
    final String message = messageSource.getMessage("message.resendToken", null, locale);
    return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
  }

  private SimpleMailMessage constructEmail(String subject, String body, User user) {
    final SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject(subject);
    email.setText(body);
    email.setTo(user.getEmail());
    return email;
  }
}