package com.matamercer.microblog.web.error.exceptions

class RevokedRefreshTokenException : RuntimeException {
    constructor() : super()
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)

    companion object {
        private const val serialVersionUID = 5861310537366287163L
    }
}