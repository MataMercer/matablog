package com.matamercer.microblog.web.error;

public class RevokedRefreshTokenException extends  RuntimeException{
        private static final long serialVersionUID = 5861310537366287163L;

        public RevokedRefreshTokenException() {
            super();
        }

        public RevokedRefreshTokenException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public RevokedRefreshTokenException(final String message) {
            super(message);
        }

        public RevokedRefreshTokenException(final Throwable cause) {
            super(cause);
        }

}
