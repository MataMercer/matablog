package com.matamercer.microblog.web.error.exceptions;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {
    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
