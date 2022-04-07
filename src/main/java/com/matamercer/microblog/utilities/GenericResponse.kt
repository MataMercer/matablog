package com.matamercer.microblog.utilities

import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError


class GenericResponse(
    private var message: String,
    private var error: String? = null
) {
    constructor(allErrors: List<ObjectError>, error: String?) : this("", error) {
        val temp = allErrors.map { e: ObjectError ->
            if (e is FieldError) {
                return@map "{\"field\":\"" + e.field + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}"
            } else {
                return@map "{\"object\":\"" + e.objectName + "\",\"defaultMessage\":\"" + e.defaultMessage + "\"}"
            }
        }.joinToString(",")
        message = "[$temp]"
    }
}

