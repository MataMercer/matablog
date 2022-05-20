package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto

fun Blog.toBlogResponseDto(): BlogResponseDto {
    val dto = toBaseModelResponseDto(BlogResponseDto())
    dto.blogName = blogName
    dto.preferredBlogName = preferredBlogName
    dto.sensitive = isSensitive
    dto.followers = followers.map { it.toFollowResponseDto() }
    dto.following = following.map { it.toFollowResponseDto() }
    return dto
}