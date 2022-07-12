package com.matamercer.microblog.web.api.v1.dto.responses

class BlogResponseDto : BaseModelResponseDto() {
    var blogName: String? = null
    var preferredBlogName: String? = null
    var sensitive = false
    var followerCount = 0
    var followingCount = 0
    var following: Boolean? = null
    var follower: Boolean? = null
}