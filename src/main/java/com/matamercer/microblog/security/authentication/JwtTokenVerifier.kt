package com.matamercer.microblog.security.authentication

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.security.authorization.UserRole
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.crypto.SecretKey
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenVerifier(
    private val secretKey: SecretKey,
    private val jwtConfig: JwtConfig,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(jwtConfig.authorizationHeader)
        if (authorizationHeader.isNullOrEmpty() || !authorizationHeader.startsWith(
                jwtConfig.tokenPrefix
            )
        ) {
            filterChain.doFilter(request, response)
            return
        }
        val token = authorizationHeader.replace(jwtConfig.tokenPrefix, "")
        try {
            val body = jwtUtil.extractAllClaims(token)
            val userId = body["userId"].toString().toLong()
            val username = body["username"].toString()
            val userRole = UserRole.valueOf(
                (body["userRole"] as String?)!!
            )
            val activeBlogId: Long = body["activeBlogId"].toString().toLong()
            val simpleGrantedAuthorities = userRole.grantedAuthorities

            val activeBlog = Blog()
            activeBlog.id = activeBlogId
            val userPrincipal = UserPrincipal(
                id = userId,
                username = username,
                activeBlog = activeBlog,
                role = userRole
            )

            val authentication = UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                simpleGrantedAuthorities
            )
            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: AuthenticationException) {
            request.setAttribute("exception", e)
        }
        filterChain.doFilter(request, response)
    }
}