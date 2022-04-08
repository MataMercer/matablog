package com.matamercer.microblog.security.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtUsernameAndPasswordAuthenticationFilter(
    private val authManager: AuthenticationManager,
    private val jwtConfig: JwtConfig,
    private val jwtUtil: JwtUtil
) : UsernamePasswordAuthenticationFilter() {
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val usernameAndPasswordAuthenticationRequest = ObjectMapper()
                .readValue(request.inputStream, UsernameAndPasswordAuthenticationRequest::class.java)
            val authentication: Authentication = UsernamePasswordAuthenticationToken(
                usernameAndPasswordAuthenticationRequest.username,
                usernameAndPasswordAuthenticationRequest.password
            )
            authManager.authenticate(authentication)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val accessToken = jwtUtil.createAccessToken(authResult.name)
        val refreshToken = jwtUtil.createRefreshToken(authResult.name)
        response.addHeader(jwtConfig.authorizationHeader, accessToken)
        response.addHeader(jwtConfig.refreshTokenHeader, refreshToken)
    }
}