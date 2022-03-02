package com.matamercer.microblog.web.api.v1.dto.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class RegisterUserRequestDto(
    var email: @Email @NotEmpty String?,
    var username: @NotEmpty String?,
    var password: @NotEmpty String?
)