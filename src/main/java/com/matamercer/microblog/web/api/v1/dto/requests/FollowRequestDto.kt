package com.matamercer.microblog.web.api.v1.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowRequestDto {
    private boolean isFollow;
    private boolean notificationsEnabled;
    private boolean muted;
}
