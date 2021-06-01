package com.matamercer.microblog.web.error;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {
    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
