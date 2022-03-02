package com.matamercer.microblog.web.api.v1.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserRequestDto {
    private String username;
    private String password;


}
