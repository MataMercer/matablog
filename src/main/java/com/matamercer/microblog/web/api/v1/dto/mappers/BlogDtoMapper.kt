package com.matamercer.microblog.web.api.v1.dto.mappers

import com.matamercer.microblog.models.entities.Blog
import com.matamercer.microblog.security.UserPrincipal
import com.matamercer.microblog.web.api.v1.dto.responses.BlogResponseDto
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

fun Blog.toBlogResponseDto(): BlogResponseDto {
    val dto = toBaseModelResponseDto(BlogResponseDto())
    dto.blogName = blogName
    dto.preferredBlogName = preferredBlogName
    dto.sensitive = isSensitive
    dto.followingCount = following.size
    dto.followerCount = followers.size
    val auth = SecurityContextHolder.getContext().authentication
    if(!auth.authorities.contains(SimpleGrantedAuthority("ROLE_ANONYMOUS"))){
        val userPrincipal = auth.principal as UserPrincipal
        dto.following = followers.any{it.follower?.id==userPrincipal.activeBlog.id}
        dto.follower = following.any{it.followee?.id == userPrincipal.activeBlog.id}
    }

    return dto
}