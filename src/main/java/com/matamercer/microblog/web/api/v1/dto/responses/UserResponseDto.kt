package com.matamercer.microblog.web.api.v1.dto.responses

import lombok.Data

@Data
class UserResponseDto : BaseModelResponseDto() {
    private val username: String? = null
    private val email: String? = null
    private val activeBlog: BlogResponseDto? = null
}