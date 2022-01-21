package com.matamercer.microblog.web.api.v1.dto.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LikeResponseDto extends BaseModelResponseDto{
    private BlogResponseDto liker;
}
