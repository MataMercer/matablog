package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.File
import com.matamercer.microblog.web.api.v1.dto.responses.FileResponseDto

fun File.toFileResponseDto(): FileResponseDto {
    val dto = toBaseModelResponseDto(FileResponseDto())
    dto.name = name
    return dto
}