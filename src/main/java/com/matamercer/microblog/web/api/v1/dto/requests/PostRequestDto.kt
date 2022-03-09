package com.matamercer.microblog.web.api.v1.dto.requests

data class PostRequestDto(
    val id: String? = null,
    val content: String? = null,
    val title: String? = null,
    val postTags: List<String>? = null,
    val communityTaggingEnabled: Boolean? = false,
    val sensitive: Boolean?,
    val published: Boolean? = false,
    val parentPostId: String? = null,
    val attachments: List<Long>? = null,
) {

}