package com.matamercer.microblog.security.authorization.oauth

import com.matamercer.microblog.models.entities.AuthenticationProvider
import com.matamercer.microblog.models.entities.User
import com.matamercer.microblog.models.repositories.UserRepository
import com.matamercer.microblog.security.authentication.JwtUtil
import com.matamercer.microblog.security.authorization.UserRole
import com.matamercer.microblog.services.BlogService
import com.matamercer.microblog.services.UserService
import com.matamercer.microblog.web.api.v1.dto.responses.AuthenticationResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException

@Service
class GithubOAuthService @Autowired constructor(
    private val githubOauthConfig: GithubOauthConfig,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val blogService: BlogService,
    private val jwtUtil: JwtUtil
) {
    fun getOrCreateOauthUser(code: String): User? {
        val accessToken = getOauthAccessToken(code)
        if (!accessToken.isNullOrBlank()) {
            val oidcUser = getOidcUser(accessToken)
            if (oidcUser != null) {
                val existingOauthUser =
                    userRepository.findByoAuth2IdAndAuthenticationProvider(oidcUser.id, AuthenticationProvider.GITHUB)
                if (existingOauthUser.isPresent) {
                    return existingOauthUser.get()
                } else {
                    val createdUser = userService.createUser(
                        User(
                            email = oidcUser.email,
                            username = oidcUser.login,
                            oAuth2Id = oidcUser.id,
                            role = UserRole.BLOGGER,
                            authenticationProvider = AuthenticationProvider.GITHUB

                        )
                    )
                    blogService.createDefaultBlogForUser(createdUser)
                }
            }
        }
        return User()
    }

    fun getOauthAccessToken(code: String): String? {
        try {
            return WebClient.create("https://github.com/")
                .post()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/login/oauth/access_token")
                        .queryParam("client_id", githubOauthConfig.clientId)
                        .queryParam("client_secret", githubOauthConfig.clientSecret)
                        .queryParam("code", code)
                        .build()
                }
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GithubAccessTokenResponseDto::class.java)
                .block()?.accessToken
        } catch (e: WebClientResponseException) {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Github Oauth failed. Could not get access token.")
        }

    }

    fun getOidcUser(accessToken: String): GithubOauthUserResponseDto? {
        try {
            return WebClient.create("https://api.github.com/")
                .get()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GithubOauthUserResponseDto::class.java)
                .block()
        } catch (e: WebClientResponseException) {
            throw ResponseStatusException(HttpStatus.BAD_GATEWAY, "Github Oauth failed. Could not get Github user.")
        }

    }

    fun getAuthentication(code: String): AuthenticationResponseDto? {
        val oauthUser = getOrCreateOauthUser(code)
        oauthUser?.let {
            oauthUser.id?.let {
                val refreshToken = jwtUtil.createRefreshToken(it)
                val accessToken = jwtUtil.createAccessToken(it)
                return AuthenticationResponseDto(accessToken, refreshToken)
            }
        }
        return null
    }


}