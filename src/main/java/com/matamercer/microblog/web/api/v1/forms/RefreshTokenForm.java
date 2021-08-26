package com.matamercer.microblog.web.api.v1.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RefreshTokenForm {
    @NotEmpty
    private String refreshToken;
}
