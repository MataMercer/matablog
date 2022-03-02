package com.matamercer.microblog.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException : RuntimeException {
    override var message: String? = null

    constructor() {}
    constructor(message: String?) {
        this.message = message
    }
}