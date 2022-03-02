package com.matamercer.microblog.web.api.v1.dto.responses

import lombok.EqualsAndHashCode
import com.matamercer.microblog.web.api.v1.dto.responses.BaseModelResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostTagResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.FileResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.LikeResponseDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto
import lombok.Data

@EqualsAndHashCode(callSuper = true)
@Data
class LikeResponseDto : BaseModelResponseDto() {
    var liker: BlogResponseDto? = null
}