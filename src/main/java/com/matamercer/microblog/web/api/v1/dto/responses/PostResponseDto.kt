package com.matamercer.microblog.web.api.v1.dto.responses

class PostResponseDto : BaseModelResponseDto() {
    var blog: BlogResponseDto? = null
    var content: String? = null
    var title: String? = null
    var postTags: List<PostTagResponseDto>? = null
    var attachments: List<FileResponseDto>? = null
    var likeCount: Int = 0
    var isLiked: Boolean? = null
    var replies: List<PostResponseDto>? = null
    var communityTaggingEnabled = false
    var sensitive = false
    var published = false
    var parentPostId: String? = null
}