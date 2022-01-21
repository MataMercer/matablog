package com.matamercer.microblog.web.api.v1.dto.responses;

import lombok.Data;

@Data
public class BaseModelResponseDto{

    private String id;
    private String createdAt;
    private String updatedAt;
}
