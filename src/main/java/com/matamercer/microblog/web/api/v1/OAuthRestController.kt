package com.matamercer.microblog.web.api.v1

import com.matamercer.microblog.security.authentication.JwtUtil
import com.matamercer.microblog.security.authorization.oauth.GithubOAuthService
import com.matamercer.microblog.security.authorization.oauth.GithubOauthConfig
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.web.api.v1.dto.responses.AuthenticationResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/api/v1/oauth")
class OAuthRestController @Autowired constructor(private val githubOAuthService: GithubOAuthService,
                                                 private val githubOauthConfig: GithubOauthConfig,
private val jwtUtil: JwtUtil){
    @GetMapping("/github")
    fun oauthLogin(
        @RequestParam(name = "code", required = true) code: String,
    ):ResponseEntity<AuthenticationResponseDto>{
        val authenticationResponseDto = githubOAuthService.getAuthentication(code)
        return ResponseEntity.ok(authenticationResponseDto)
    }

    @GetMapping("/github/login")
    fun redirectOauthSignInPage(response: HttpServletResponse){
        val uriBuilder = UriComponentsBuilder
            .fromUriString("https://github.com/login/oauth/authorize")
            .queryParam("client_id", githubOauthConfig.clientId)
            .queryParam("scope", "user")
        response.sendRedirect(uriBuilder.toUriString())
    }
}