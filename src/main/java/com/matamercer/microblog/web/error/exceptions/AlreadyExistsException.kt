package com.matamercer.microblog.web.error.exceptions

class AlreadyExistsException : RuntimeException {
    constructor() : super()
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
}