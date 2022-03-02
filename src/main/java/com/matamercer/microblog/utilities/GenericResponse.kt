package com.matamercer.microblog.utilities

import lombok.Getter
import org.springframework.beans.factory.annotation.Autowired
import kotlin.Throws
import java.net.InetAddress
import org.springframework.validation.ObjectError
import org.springframework.validation.FieldError
import java.util.stream.Collectors
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
class GenericResponse {
    private var message: String
    private var error: String? = null

    constructor(message: String) : super() {
        this.message = message
    }

    constructor(message: String, error: String?) : super() {
        this.message = message
        this.error = error
    }

    constructor(allErrors: List<ObjectError>, error: String?) {
        this.error = error
        val temp = allErrors.stream().map { e: ObjectError ->
            if (e is FieldError) {
                return@map "{\"field\":\"" + e.field + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}"
            } else {
                return@map "{\"object\":\"" + e.objectName + "\",\"defaultMessage\":\"" + e.defaultMessage + "\"}"
            }
        }.collect(Collectors.joining(","))
        message = "[$temp]"
    }
}