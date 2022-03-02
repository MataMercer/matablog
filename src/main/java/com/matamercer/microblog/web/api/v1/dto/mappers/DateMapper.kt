package com.matamercer.microblog.web.api.v1.dto.mappers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.asString(): String? {
    return format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}