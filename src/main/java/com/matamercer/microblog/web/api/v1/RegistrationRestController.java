package com.matamercer.microblog.web.api.v1;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.matamercer.microblog.forms.RegisterUserForm;
import com.matamercer.microblog.models.entities.AuthenticationProvider;
import com.matamercer.microblog.models.entities.User;
import com.matamercer.microblog.models.entities.VerificationToken;
import com.matamercer.microblog.models.repositories.UserRepository;
import com.matamercer.microblog.security.OnRegistrationCompleteEvent;
import com.matamercer.microblog.security.UserRole;
import com.matamercer.microblog.services.UserService;
import com.matamercer.microblog.utilities.GenericResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RestController
@RequestMapping("/api/user")
public class RegistrationRestController {
  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final MessageSource messageSource;
  private final JavaMailSender mailSender;

  @Autowired
  public RegistrationRestController(UserRepository userRepository, UserService userService,
      PasswordEncoder passwordEncoder, ApplicationEventPublisher applicationEventPublisher, MessageSource messageSource,
      JavaMailSender mailSender) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.applicationEventPublisher = applicationEventPublisher;
    this.messageSource = messageSource;
    this.mailSender = mailSender;
  }

  @PostMapping("/registration")
  public GenericResponse registerUserAccount(@Valid RegisterUserForm registerUserForm, HttpServletRequest request) {
    User registeredUser = new User(
            registerUserForm.getEmail(),
            registerUserForm.getUsername(),
            passwordEncoder.encode(registerUserForm.getPassword()),
            true,
            true,
            true,
            false,
            AuthenticationProvider.LOCAL);
    registeredUser = userService.createUser(registeredUser, UserRole.USER);
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

  @PostMapping("/test")
  public GenericResponse test(HttpServletRequest request) {
    return new GenericResponse("derp");
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