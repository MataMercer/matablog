package com.matamercer.microblog.web.api.v1.dto.responses;

import com.matamercer.microblog.models.entities.Blog;
import lombok.Data;

import java.util.List;

@Data
public class PostResponseDto {
    private String id;

    private Blog blog;

    private String content;

    private String title;

    private List<String> postTags;

    private boolean communityTaggingEnabled;

    private boolean sensitive;

    private boolean published;

    private String parentPostId;

    private int likeCount;

    private int replyCount;
}
