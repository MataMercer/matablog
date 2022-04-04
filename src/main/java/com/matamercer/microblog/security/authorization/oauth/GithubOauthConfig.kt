package com.matamercer.microblog.security.authorization.oauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application.oauth.github")
class GithubOauthConfig {
    var clientId: String? = null
    var clientSecret: String? = null
}