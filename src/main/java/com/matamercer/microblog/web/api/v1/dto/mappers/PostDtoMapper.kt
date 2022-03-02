package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto

fun PostRequestDto.toPost(): Post {
    val post = Post()
        post.id = id?.toLong()
        post.content = content
        post.title = title
        post.isSensitive = sensitive ?: false
        post.published = published ?: false
    return post
}

fun Post.toPostResponseDto(): PostResponseDto {
    val dto = toBaseModelResponseDto(PostResponseDto())
    dto.blog = blog?.toBlogResponseDto()
    dto.content = content
    dto.title = title
    dto.postTags = postTags.map { it.toPostTagResponseDto() }
    dto.attachments = attachments.map { it.toFileResponseDto() }
    dto.likes = likes.map { it.toLikeResponseDto() }
    dto.replies = replies.map { it.toPostResponseDto() }
    dto.communityTaggingEnabled = isCommunityTaggingEnabled
    dto.sensitive = isSensitive
    dto.published = published
    dto.parentPostId = parentPost?.id.toString()

    return dto
}