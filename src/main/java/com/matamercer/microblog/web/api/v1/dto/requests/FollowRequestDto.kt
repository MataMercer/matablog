package com.matamercer.microblog.web.api.v1.dto.requests

class FollowRequestDto(
    var isFollow: Boolean,
    var notificationsEnabled: Boolean,
    var muted: Boolean)