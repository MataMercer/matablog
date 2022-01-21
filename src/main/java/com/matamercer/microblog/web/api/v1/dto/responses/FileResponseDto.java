package com.matamercer.microblog.web.api.v1.dto.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileResponseDto extends BaseModelResponseDto{
    private String name;
}
