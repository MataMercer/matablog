package com.matamercer.microblog.web.api.v1.dto.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BlogResponseDto extends BaseModelResponseDto{
    private String blogName;
    private String preferredBlogName;
    private boolean sensitive;
}
