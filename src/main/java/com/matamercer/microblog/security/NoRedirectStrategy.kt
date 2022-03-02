package com.matamercer.microblog.security

import org.springframework.security.web.RedirectStrategy
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

internal class NoRedirectStrategy : RedirectStrategy {
    @Throws(IOException::class)
    override fun sendRedirect(request: HttpServletRequest, response: HttpServletResponse, url: String) {
        // No redirect is required with pure REST
    }
}