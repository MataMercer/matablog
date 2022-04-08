package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.BaseModel
import com.matamercer.microblog.web.api.v1.dto.responses.BaseModelResponseDto

fun <T : BaseModelResponseDto> BaseModel.toBaseModelResponseDto(dto: T): T {
    dto.id = id.toString()
    dto.createdAt = createdAt?.asString()
    dto.updatedAt = updatedAt?.asString()
    return dto
}

