package com.matamercer.microblog.web

import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.services.PostService
import com.matamercer.microblog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
@RequestMapping("/")
class UserController @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val postService: PostService,
    private val messageSource: MessageSource
) {
    @GetMapping("/registration/confirm")
    fun confirmRegistration(
        request: WebRequest,
        model: Model,
        @RequestParam("token") token: String?,
        redirectAttributes: RedirectAttributes
    ): String {
        val locale = request.locale
        val verificationToken = userService.getVerificationToken(token)
        if (verificationToken == null) {
            val message = messageSource.getMessage("auth.message.invalidToken", null, locale)
            model.addAttribute("message", message)
            return "redirect:/badUser.html?lang=" + locale.language
        }
        val user = verificationToken.user
        val cal = Calendar.getInstance()
        if (verificationToken.expiryDate.time - cal.time.time <= 0) {
            val messageValue = messageSource.getMessage("auth.message.expired", null, locale)
            model.addAttribute("message", messageValue)
            return "redirect:/baduser.html?lang=" + locale.language
        }
        user!!.enabled = true
        userRepository.save(user)
        redirectAttributes.addFlashAttribute(
            "message",
            messageSource.getMessage("message.regSuccConfirmed", null, locale)
        )
        return "redirect:/login"
    }

    companion object {
        private const val PAGE_SIZE = 40
    }
}