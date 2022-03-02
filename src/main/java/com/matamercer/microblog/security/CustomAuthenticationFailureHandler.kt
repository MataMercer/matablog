package com.matamercer.microblog.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component("authenticationFailureHandler")
class CustomAuthenticationFailureHandler @Autowired constructor(
    private val messageSource: MessageSource,
    private val localeResolver: LocaleResolver
) : SimpleUrlAuthenticationFailureHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        setDefaultFailureUrl("/login?error=true")
        super.onAuthenticationFailure(request, response, exception)
        val locale = localeResolver.resolveLocale(request)
        var errorMessage = messageSource.getMessage("message.badCredentials", null, locale)
        if (exception.message.equals("User is disabled", ignoreCase = true)) {
            errorMessage = messageSource.getMessage("auth.message.disabled", null, locale)
        } else if (exception.message.equals("User account has expired", ignoreCase = true)) {
            errorMessage = messageSource.getMessage("auth.message.expired", null, locale)
        } else if (exception.message.equals("blocked", ignoreCase = true)) {
            errorMessage = messageSource.getMessage("auth.message.blocked", null, locale)
        } else if (exception.message.equals("unusual location", ignoreCase = true)) {
            errorMessage = messageSource.getMessage("auth.message.unusual.location", null, locale)
        }
        request.session
            .setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage)
    }
}