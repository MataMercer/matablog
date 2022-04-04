package com.matamercer.microblog.security.authorization.oauth

import com.fasterxml.jackson.annotation.JsonProperty

class GithubAccessTokenResponseDto(
    @JsonProperty("access_token")
    var accessToken: String?,
    @JsonProperty("scope")
    var scope: String?,
    @JsonProperty("token_type")
    var tokenType: String?
)
