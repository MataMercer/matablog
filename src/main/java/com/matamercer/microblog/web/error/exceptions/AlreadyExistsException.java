package com.matamercer.microblog.web.error.exceptions;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(final String message) {
        super(message);
    }

    public AlreadyExistsException(final Throwable cause) {
        super(cause);
    }
}
