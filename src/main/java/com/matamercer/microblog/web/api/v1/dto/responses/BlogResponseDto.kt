package com.matamercer.microblog.web.api.v1.dto.responses

class BlogResponseDto : BaseModelResponseDto() {
    var blogName: String? = null
    var preferredBlogName: String? = null
    var sensitive = false
    var followers: List<FollowResponseDto>? = null
    var following: List<FollowResponseDto>? = null
}