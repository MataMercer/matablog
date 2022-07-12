package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.Post
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.web.api.v1.dto.requests.PostRequestDto
import com.matamercer.microblog.web.api.v1.dto.responses.PostResponseDto
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

fun PostRequestDto.toPost(): Post {
    val post = Post()
        post.id = id?.toLong() ?: 0
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
    dto.likeCount = likes.size
    dto.replies = replies.map { it.toPostResponseDto() }
    dto.communityTaggingEnabled = isCommunityTaggingEnabled
    dto.sensitive = isSensitive
    dto.published = published
    dto.parentPostId = parentPost?.id.toString()
    val auth = SecurityContextHolder.getContext().authentication
    if(!auth.authorities.contains(SimpleGrantedAuthority("ROLE_ANONYMOUS"))){
        val userPrincipal = auth.principal as UserPrincipal
        dto.isLiked = likes.any{ it.liker?.id == userPrincipal.activeBlog.id}
    }

    return dto
}