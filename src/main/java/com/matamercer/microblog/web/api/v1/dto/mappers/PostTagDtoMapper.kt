package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.PostTag
import com.matamercer.microblog.web.api.v1.dto.responses.PostTagResponseDto

fun PostTag.toPostTagResponseDto(): PostTagResponseDto {
    val dto = toBaseModelResponseDto(PostTagResponseDto())
    dto.name = name
    return dto
}