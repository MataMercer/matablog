package com.matamercer.microblog.web.api.v1.dto.responses;

import lombok.Data;

@Data
public class UserResponseDto {
    private String username;
    private String email;
    private BlogResponseDto activeBlog;
}
