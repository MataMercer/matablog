package com.matamercer.microblog.web.api.v1.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RefreshTokenRequestDto {
    @NotEmpty
    private String refreshToken;
}
