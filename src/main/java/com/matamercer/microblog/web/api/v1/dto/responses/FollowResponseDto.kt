package com.matamercer.microblog.web.api.v1.dto.responses

class FollowResponseDto(
    var follower: BlogResponseDto? = null,
    var followee: BlogResponseDto? = null,
    var notificationsEnabled: Boolean = false,
    var muted: Boolean = false,
):BaseModelResponseDto()