package com.matamercer.microblog.web.error.exceptions

class AuthenticationException(msg: String?, cause: Throwable?) :
    org.springframework.security.core.AuthenticationException(msg, cause)