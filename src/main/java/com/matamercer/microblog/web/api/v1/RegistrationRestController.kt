package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.entities.VerificationToken
import com.matamercer.microblog.security.OnRegistrationCompleteEvent
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.BlogService
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.utilities.GenericResponse
import com.matamercer.microblog.web.api.v1.dto.requests.RegisterUserRequestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.MessageSource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/registration")
class RegistrationRestController @Autowired constructor(
    private val userService: UserService,
    private val blogService: BlogService,
    private val passwordEncoder: PasswordEncoder,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val messageSource: MessageSource,
    private val mailSender: JavaMailSender
) {
    @PostMapping("/createAccount")
    fun registerUserAccount(
        @RequestBody registerUserRequestDTO: @Valid RegisterUserRequestDto?,
        request: HttpServletRequest
    ): GenericResponse {
        var registeredUser = User(
            email = registerUserRequestDTO!!.email,
            username = registerUserRequestDTO.username,
            password = passwordEncoder.encode(registerUserRequestDTO.password),
            role = UserRole.BLOGGER,
            authenticationProvider = AuthenticationProvider.LOCAL
        )
        registeredUser = userService.createUser(registeredUser)
        blogService.createDefaultBlogForUser(registeredUser)
        applicationEventPublisher
            .publishEvent(OnRegistrationCompleteEvent(registeredUser, request.locale, request.contextPath))
        return GenericResponse("success")
    }

    @GetMapping("/resendRegistrationToken")
    fun resendRegistrationToken(
        request: HttpServletRequest,
        @RequestParam("token") existingToken: String?
    ): GenericResponse {
        val newToken = userService.generateNewVerificationToken(existingToken)
        val user = userService.getUser(newToken)
        val appUrl = "http://" + request.serverName + ":" + request.serverPort + request.contextPath
        val email = constructResendVerificationTokenEmail(appUrl, request.locale, newToken, user)
        mailSender.send(email)
        return GenericResponse(messageSource.getMessage("message.resendToken", null, request.locale))
    }

    // Helper Methods
    private fun constructResendVerificationTokenEmail(
        contextPath: String, locale: Locale,
        newToken: VerificationToken?, user: User?
    ): SimpleMailMessage {
        val confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken!!.token
        val message = messageSource.getMessage("message.resendToken", null, locale)
        return constructEmail("Resend Registration Token", "$message \r\n$confirmationUrl", user)
    }

    private fun constructEmail(subject: String, body: String, user: User?): SimpleMailMessage {
        val email = SimpleMailMessage()
        email.subject = subject
        email.text = body
        email.setTo(user!!.email)
        return email
    }
}