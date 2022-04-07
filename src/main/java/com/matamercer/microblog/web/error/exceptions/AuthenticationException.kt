package com.matamercer.microblog.web.error.exceptions

import org.springframework.security.core.AuthenticationException

class AuthenticationException(msg: String?, cause: Throwable?) :
    AuthenticationException(msg, cause)