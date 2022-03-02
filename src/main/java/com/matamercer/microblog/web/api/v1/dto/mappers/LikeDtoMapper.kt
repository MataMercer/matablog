package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.Like
import com.matamercer.microblog.web.api.v1.dto.responses.LikeResponseDto

fun Like.toLikeResponseDto(): LikeResponseDto {
    val dto = toBaseModelResponseDto(LikeResponseDto())
    dto.liker = liker?.toBlogResponseDto()
    return dto
}