package com.matamercer.microblog.security.authorization.oauth

import com.fasterxml.jackson.annotation.JsonProperty

class GithubOauthUserResponseDto(
    @JsonProperty("login")
    val login: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("email")
    val email: String
)