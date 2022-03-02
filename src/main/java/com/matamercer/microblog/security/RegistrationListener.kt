package com.matamercer.microblog.security

import com.matamercer.microblog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.MessageSource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegistrationListener : ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private val messageSource: MessageSource? = null

    @Autowired
    private val mailSender: JavaMailSender? = null

    @Autowired
    private val userService: UserService? = null
    override fun onApplicationEvent(event: OnRegistrationCompleteEvent) {
        confirmRegistration(event)
    }

    private fun confirmRegistration(event: OnRegistrationCompleteEvent) {
        val user = event.user
        val token = UUID.randomUUID().toString()
        userService!!.createVerificationTokenForUser(user, token)
        val recipientAddress = user.email
        val subject = "Registration Confirmation"
        val confirmationUrl = event.appUrl + "/registration/confirm?token=" + token
        val message = messageSource!!.getMessage("message.regSucc", null, event.locale)
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(recipientAddress)
        simpleMailMessage.subject = subject
        simpleMailMessage.text = "$message\r\nhttps://localhost:8443$confirmationUrl"
        mailSender!!.send(simpleMailMessage)
    }
}