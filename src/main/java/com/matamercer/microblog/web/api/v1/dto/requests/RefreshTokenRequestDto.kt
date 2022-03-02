package com.matamercer.microblog.web.api.v1.dto.requests

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class RefreshTokenRequestDto(
    var refreshToken:
    String? = null )