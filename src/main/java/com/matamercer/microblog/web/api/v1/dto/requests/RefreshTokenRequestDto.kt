package com.matamercer.microblog.web.api.v1.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RefreshTokenRequestDto {
    @NotNull
    @NotBlank(message="Refresh token is required")
    @NotEmpty
    private String refreshToken;
}
